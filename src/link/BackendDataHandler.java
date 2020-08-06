package link;

import definitions.DefinitionsManager;
import link.instructions.*;
import main.Engine;
import main.LiveLog;
import main.LogHub;
import user.UserAccount;
import user.UserAccountManager;

import static link.instructions.LogInResponseInstructionDatum.*;
import static user.UserAccountManager.*;

public class BackendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(DataLink dataLink) {
        //todo - better handling here - wait a certain time for reconnection.
        // Also call the OrderExecutor's disconnect method on this link to handle orders.
        dataLink.terminate(); //stop thread execution
        if (dataLink instanceof LocalDataLink)
            LogHub.logFatalCrash("Local connection lost", new IllegalStateException());
        LiveLog.log("Lost connection to client.", LiveLog.LogEntryPriority.WARNING);
        Engine.getInstance().disconnectDataLink(dataLink); //instruct the engine to properly remove the link
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof AccountCreationRequestInstructionDatum) {
            AccountCreationRequestInstructionDatum acrid = (AccountCreationRequestInstructionDatum)instructionDatum;
            UserAccount createdAccount =
                    UserAccountManager.createUserAccount(acrid.USERNAME, acrid.SALT, acrid.HASHED_PASSWORD);
            responseLink.transmit(
                    new LogInResponseInstructionDatum(
                            createdAccount == null ? LOGIN_FAILURE_DUPLICATE_ACCOUNT_CREATION : LOGIN_SUCCESS,
                            createdAccount
                    )
            );
            if (createdAccount != null)
                Engine.getInstance().connectUserAccount(responseLink, createdAccount);
        } else if (instructionDatum instanceof LogInRequestInstructionDatum) {
            LogInRequestInstructionDatum lirid = (LogInRequestInstructionDatum)instructionDatum;
            String[] catalogFields = UserAccountManager.queryUsername(lirid.USERNAME);
            if (catalogFields == null) {
                responseLink.transmit(
                        new LogInResponseInstructionDatum(
                                LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST,
                                null
                        )
                );
            } else {
                UserAccount loggedInAccount = UserAccount.load(catalogFields[USERNAME]);
                if (Engine.getInstance().isConnected(loggedInAccount)) {
                    /*
                     * Check for a duplicate login prior to other checks.
                     * This way the error message does not reveal whether the duplicate login attempt used the correct
                     * password.
                     */
                    responseLink.transmit(
                            new LogInResponseInstructionDatum(
                                    LOGIN_FAILURE_ACCOUNT_ALREADY_CONNECTED,
                                    null
                            )
                    );
                } else if (!catalogFields[HASHED_PASSWORD].equals(lirid.getHashedPassword(catalogFields[SALT]))) {
                    responseLink.transmit(
                            new LogInResponseInstructionDatum(
                                    LOGIN_FAILURE_INCORRECT_PASSWORD,
                                    null
                            )
                    );
                } else {
                    responseLink.transmit(
                            new LogInResponseInstructionDatum(
                                    LOGIN_SUCCESS,
                                    loggedInAccount
                            )
                    );
                    Engine.getInstance().connectUserAccount(responseLink, loggedInAccount);
                }
            }
        } else if (instructionDatum instanceof LogOutInstructionDatum) {
            responseLink.terminate(); //stop thread execution
            LogOutInstructionDatum lorid = (LogOutInstructionDatum)instructionDatum;
            LiveLog.log("User \"" + lorid.USERNAME + "\" requested logout.", LiveLog.LogEntryPriority.INFO);
            Engine.getInstance().disconnectDataLink(responseLink); //instruct the engine to properly remove the link
        } else if (instructionDatum instanceof OrderTransmissionInstructionDatum){
            OrderTransmissionInstructionDatum otid = (OrderTransmissionInstructionDatum)instructionDatum;
            if (otid.ORDER == null)
                DefinitionsManager.executeOrder().clearOrder(responseLink, otid.ORDER_CLASS);
            else
                DefinitionsManager.executeOrder().setOrder(responseLink, otid.ORDER);
        } else if (instructionDatum instanceof ReportChecksumMismatchInstructionDatum) {
            responseLink.transmit(new GameZoneInstructionDatum(Engine.getInstance().getGameZone(responseLink)));
        } else if (instructionDatum instanceof SelectAvatarInstructionData) {
            Engine.getInstance().connectUserAvatar(
                    responseLink,
                    ((SelectAvatarInstructionData)instructionDatum).USER_AVATAR
            );
        }
    }
}
