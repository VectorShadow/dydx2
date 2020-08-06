package link;

import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import link.instructions.*;
import main.LiveLog;
import main.LogHub;
import user.UserAccount;

import static link.instructions.LogInResponseInstructionDatum.*;
import static main.LiveLog.LogEntryPriority.*;

public class FrontendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(DataLink dataLink) {
        if (dataLink instanceof LocalDataLink)
            LogHub.logFatalCrash("Local connection lost", new IllegalStateException());
        LiveLog.log("Lost connection to remote server.", LiveLog.LogEntryPriority.WARNING);
        //todo - try to re-establish? for now, close the program.
        System.exit(-3);
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof GameZoneInstructionDatum) {
            GameZone.frontEnd = ((GameZoneInstructionDatum)instructionDatum).GAME_ZONE;
            LiveLog.log("Loaded new gameZone on frontend.", INFO);
        } else if (instructionDatum instanceof GameZoneUpdateInstructionDatum) {
            GameZoneUpdateInstructionDatum gzuid = ((GameZoneUpdateInstructionDatum) instructionDatum);
            for (GameZoneUpdate gzu : gzuid.UPDATE_LIST)
                GameZone.frontEnd.apply(gzu);
            if (GameZone.frontEnd.getCheckSum() != gzuid.UPDATE_CHECKSUM) {
                responseLink.transmit(new ReportChecksumMismatchInstructionDatum());
                LiveLog.log("Game update checksum validation failed! Requesting updated game zone.", WARNING);
            }
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
                    UserAccount.activeSession = lirid.USER_ACCOUNT;
                    break;
                    default:
                        throw new IllegalStateException("Unhandled response code: " + lirid.RESPONSE_CODE);
            }
        } else {
                //todo - more cases
                throw new IllegalArgumentException("Unhandled InstructionDatum class: " + instructionDatum.getClass());
        }
    }
}
