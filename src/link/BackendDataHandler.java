package link;

import link.instructions.AccountCreationRequestInstructionDatum;
import link.instructions.InstructionDatum;
import link.instructions.LoginRequestInstructionDatum;
import link.instructions.LoginResponseInstructionDatum;
import main.Engine;
import user.UserAccount;
import user.UserAccountManager;

import java.net.Socket;

import static link.instructions.LoginResponseInstructionDatum.*;
import static user.UserAccountManager.*;

public class BackendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof LoginRequestInstructionDatum) {
            LoginRequestInstructionDatum lrid = (LoginRequestInstructionDatum)instructionDatum;
            String[] catalogFields = UserAccountManager.queryUsername(lrid.USERNAME);
            if (catalogFields == null) {
                responseLink.transmit(
                        new LoginResponseInstructionDatum(
                                LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST,
                                null
                        )
                );
            } else if (!catalogFields[HASHED_PASSWORD].equals(lrid.getHashedPassword(catalogFields[SALT]))) {
                responseLink.transmit(
                        new LoginResponseInstructionDatum(
                                LOGIN_FAILURE_INCORRECT_PASSWORD,
                                null
                        )
                );
            } else {
                UserAccount loggedInAccount = UserAccount.load(catalogFields[USERNAME]);
                responseLink.transmit(
                        new LoginResponseInstructionDatum(
                                LOGIN_SUCCESS,
                                loggedInAccount
                        )
                );
                Engine.getInstance().getDataLinkSession(responseLink).setUserAccount(loggedInAccount);
            }
        } else if (instructionDatum instanceof AccountCreationRequestInstructionDatum) {
            AccountCreationRequestInstructionDatum acrid = (AccountCreationRequestInstructionDatum)instructionDatum;
            UserAccount createdAccount =
                    UserAccountManager.createUserAccount(acrid.USERNAME, acrid.SALT, acrid.HASHED_PASSWORD);
            responseLink.transmit(
                    new LoginResponseInstructionDatum(
                            LOGIN_SUCCESS,
                            createdAccount
                    )
            );
            Engine.getInstance().getDataLinkSession(responseLink).setUserAccount(createdAccount);
        }
    }
}
