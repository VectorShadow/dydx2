package link;

import link.instructions.*;
import main.Engine;
import user.UserAccount;
import user.UserAccountManager;

import java.net.Socket;

import static link.instructions.LogInResponseInstructionDatum.*;
import static user.UserAccountManager.*;

public class BackendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof LogInRequestInstructionDatum) {
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
        } else if (instructionDatum instanceof LogOutRequestInstructionDatum) {
            LogOutRequestInstructionDatum lorid = (LogOutRequestInstructionDatum)instructionDatum;
            Engine.getInstance().disconnectUserAccount(responseLink, lorid.USERNAME);
            //no need to send a response here
        } else if (instructionDatum instanceof AccountCreationRequestInstructionDatum) {
            AccountCreationRequestInstructionDatum acrid = (AccountCreationRequestInstructionDatum)instructionDatum;
            UserAccount createdAccount =
                    UserAccountManager.createUserAccount(acrid.USERNAME, acrid.SALT, acrid.HASHED_PASSWORD);
            responseLink.transmit(
                    new LogInResponseInstructionDatum(
                            LOGIN_SUCCESS,
                            createdAccount
                    )
            );
            Engine.getInstance().connectUserAccount(responseLink, createdAccount);
        }
    }
}
