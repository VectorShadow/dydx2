package link;

import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import link.instructions.GameZoneUpdateInstructionDatum;
import link.instructions.InstructionDatum;
import link.instructions.LoginResponseInstructionDatum;

import static link.instructions.LoginResponseInstructionDatum.*;

import java.net.Socket;

public class FrontendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof GameZoneUpdateInstructionDatum) {
            for (GameZoneUpdate gzu : ((GameZoneUpdateInstructionDatum) instructionDatum).UPDATE_LIST)
                GameZone.frontEnd.apply(gzu);
            //todo - send back a checksum
        } else if (instructionDatum instanceof LoginResponseInstructionDatum) {
            LoginResponseInstructionDatum lrid = (LoginResponseInstructionDatum)instructionDatum;
            switch (lrid.RESPONSE_CODE) {
                case LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST:
                    System.out.println("Account did not exist!");
                    //todo - proper user notification
                    break;
                case LOGIN_FAILURE_INCORRECT_PASSWORD:
                    System.out.println("Incorrect password!");
                    //todo - proper user notification
                    break;
                case LOGIN_SUCCESS:
                    System.out.println("Login successful!");
                    //todo - progress the game to character selection, probably.
                    //todo - use the transmitted user account for this
                    break;
                    default:
                        throw new IllegalStateException("Unhandled response code: " + lrid.RESPONSE_CODE);
            }

        } else {
                //todo - more cases
                throw new IllegalArgumentException("Unhandled InstructionDatum class: " + instructionDatum.getClass());
        }
    }
}
