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
        DefinitionsManager.getOrderExecutor().backEndHandleDisconnection(dataLink); //currently redundant since we purge the link, but should be maintained once we handle disconnection more appropriately
        dataLink.terminate(); //stop thread execution
        if (dataLink instanceof LocalDataLink)
            LogHub.logFatalCrash("Local connection lost", new IllegalStateException());
        LiveLog.log("Lost connection to client.", LiveLog.LogEntryPriority.WARNING);
        Engine.getInstance().disconnectDataLink(dataLink); //instruct the engine to properly remove the link
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        UserAccount loggedInAccount;
        if (instructionDatum instanceof AccountCreationRequestInstructionDatum) {
            AccountCreationRequestInstructionDatum acrid = (AccountCreationRequestInstructionDatum)instructionDatum;
            UserAccount createdAccount =
                    UserAccountManager.createUserAccount(acrid.USERNAME, acrid.SALT, acrid.HASHED_PASSWORD);
            responseLink.transmit(
                    new LogInResponseInstructionDatum(
                            createdAccount == null ? null : createdAccount.buildMetadata(),
                            createdAccount == null ? LOGIN_FAILURE_DUPLICATE_ACCOUNT_CREATION : LOGIN_SUCCESS
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
                                null,
                                LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST
                        )
                );
            } else {
                loggedInAccount = UserAccount.load(catalogFields[USERNAME]);
                if (Engine.getInstance().isConnected(loggedInAccount)) {
                    /*
                     * Check for a duplicate login prior to other checks.
                     * This way the error message does not reveal whether the duplicate login attempt used the correct
                     * password.
                     */
                    responseLink.transmit(
                            new LogInResponseInstructionDatum(
                                    null,
                                    LOGIN_FAILURE_ACCOUNT_ALREADY_CONNECTED
                            )
                    );
                } else if (!catalogFields[HASHED_PASSWORD].equals(lirid.getHashedPassword(catalogFields[SALT]))) {
                    responseLink.transmit(
                            new LogInResponseInstructionDatum(
                                    null,
                                    LOGIN_FAILURE_INCORRECT_PASSWORD
                            )
                    );
                } else {
                    responseLink.transmit(
                            new LogInResponseInstructionDatum(
                                    loggedInAccount.buildMetadata(),
                                    LOGIN_SUCCESS
                            )
                    );
                    Engine.getInstance().connectUserAccount(responseLink, loggedInAccount);
                }
            }
        } else if (instructionDatum instanceof LogOutInstructionDatum) {
            LogOutInstructionDatum lorid = (LogOutInstructionDatum)instructionDatum;
            LiveLog.log(
                    "User \"" + Engine.getInstance().getUserAccount(responseLink).getName() +
                    "\" requested logout.", LiveLog.LogEntryPriority.INFO
            );
            responseLink.transmit(new LogOutInstructionDatum()); //send logout confirmation
            responseLink.terminate(); //stop thread execution
            Engine.getInstance().disconnectDataLink(responseLink); //instruct the engine to properly remove the link
        } else if (instructionDatum instanceof OrderTransmissionInstructionDatum){
            OrderTransmissionInstructionDatum otid = (OrderTransmissionInstructionDatum)instructionDatum;
            if (otid.ORDER == null)
                DefinitionsManager.getOrderExecutor().clearOrder(responseLink, otid.ORDER_CLASS);
            else
                DefinitionsManager.getOrderExecutor().setOrder(responseLink, otid.ORDER);
        } else if (instructionDatum instanceof ReportChecksumMismatchInstructionDatum) {
            responseLink.transmit(new GameZoneInstructionDatum(Engine.getInstance().getGameZone(responseLink)));
        } else if (instructionDatum instanceof SelectAvatarInstructionData) {
            int avatarIndex = ((SelectAvatarInstructionData)instructionDatum).AVATAR_INDEX;
            loggedInAccount = Engine.getInstance().getUserAccount(responseLink);
            if (avatarIndex < 0) {
                avatarIndex =
                        loggedInAccount
                        .addAvatar(
                                DefinitionsManager.
                                        getAvatarManager().
                                        createNewAvatar(
                                                avatarIndex,
                                                ((SelectAvatarInstructionData)instructionDatum).NAME
                                        )
                        );
            }
            //Here, engine selects the specified avatar from its account, then places an appropriate actor in its gamezone
            int actorID = Engine.getInstance().connectUserAvatar(responseLink, avatarIndex);
            //finally, send the frontend a datum specifying which actor serial ID in its gamezone belongs to it
            responseLink.transmit(
                    new IdentifyAvatarAndActorInstructionDatum(
                            loggedInAccount.buildMetadata(),
                            actorID,
                            avatarIndex
                    )
            );
        }
    }
}
