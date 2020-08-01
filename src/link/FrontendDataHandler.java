package link;

import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import link.instructions.*;

import static link.instructions.LogInResponseInstructionDatum.*;

import java.net.Socket;

public class FrontendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof GameZoneInstructionDatum) {
            GameZone.frontEnd = ((GameZoneInstructionDatum)instructionDatum).GAME_ZONE;
        } else if (instructionDatum instanceof GameZoneUpdateInstructionDatum) {
            GameZoneUpdateInstructionDatum gzuid = ((GameZoneUpdateInstructionDatum) instructionDatum);
            for (GameZoneUpdate gzu : gzuid.UPDATE_LIST)
                GameZone.frontEnd.apply(gzu);
            if (GameZone.frontEnd.getCheckSum() != gzuid.UPDATE_CHECKSUM)
                responseLink.transmit(new ReportChecksumMismatchInstructionDatum());
        } else if (instructionDatum instanceof LogInResponseInstructionDatum) {
            LogInResponseInstructionDatum lirid = (LogInResponseInstructionDatum)instructionDatum;
            switch (lirid.RESPONSE_CODE) {
                case LOGIN_FAILURE_DUPLICATE_ACCOUNT_CREATION:
                    System.out.println("Duplicate account creation!");
                    break;
                case LOGIN_FAILURE_ACCOUNT_ALREADY_CONNECTED:
                    System.out.println("Account was already connected!");
                    //todo - proper user notification
                    break;
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
                        throw new IllegalStateException("Unhandled response code: " + lirid.RESPONSE_CODE);
            }
        } else if (instructionDatum instanceof LogOutInstructionDatum) {
            responseLink.forceExpiration();
        } else {
                //todo - more cases
                throw new IllegalArgumentException("Unhandled InstructionDatum class: " + instructionDatum.getClass());
        }
    }
}
