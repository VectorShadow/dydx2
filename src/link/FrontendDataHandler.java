package link;

import definitions.DefinitionsManager;
import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import link.instructions.*;
import main.LiveLog;
import main.LogHub;
import user.UserAccountManager;

import static link.instructions.LogInResponseInstructionDatum.*;
import static main.LiveLog.LogEntryPriority.*;

public class FrontendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(DataLink dataLink) {
        if (dataLink instanceof LocalDataLink)
            LogHub.logFatalCrash("Local connection lost", new IllegalStateException());
        LiveLog.log("Lost connection to remote server.", LiveLog.LogEntryPriority.WARNING);
        //todo - try to re-establish? for now, close the program.
        DefinitionsManager.getOrderExecutor().frontEndHandleDisconnection(); //currently redundant since we exit the program, but should be maintained once we handle disconnection more appropriately
        System.exit(-3);
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionDatum instanceof GameZoneInstructionDatum) {
            GameZone.frontEnd = ((GameZoneInstructionDatum)instructionDatum).GAME_ZONE;
            DefinitionsManager.getGameZoneUpdateListener().changeGameZone();
            LiveLog.log("Loaded new gameZone on frontend.", INFO);
        } else if (instructionDatum instanceof GameZoneUpdateInstructionDatum) {
            GameZoneUpdateInstructionDatum gzuid = ((GameZoneUpdateInstructionDatum) instructionDatum);
            for (GameZoneUpdate gzu : gzuid.UPDATE_LIST)
                GameZone.frontEnd.apply(gzu);
            DefinitionsManager.getGameZoneUpdateListener().updateGameZone();
            if (GameZone.frontEnd.getCheckSum() != gzuid.UPDATE_CHECKSUM) {
                responseLink.transmit(new ReportChecksumMismatchInstructionDatum());
                LiveLog.log("Game update checksum validation failed! Requesting updated game zone.", WARNING);
            }
        } else if (instructionDatum instanceof LogInResponseInstructionDatum) {
            LogInResponseInstructionDatum lirid = (LogInResponseInstructionDatum)instructionDatum;
            switch (lirid.RESPONSE_CODE) {
                case LOGIN_FAILURE_DUPLICATE_ACCOUNT_CREATION:
                    DefinitionsManager.getLoginResponseHandler().loginResponseDuplicateAccountCreation();
                    break;
                case LOGIN_FAILURE_ACCOUNT_ALREADY_CONNECTED:
                    DefinitionsManager.getLoginResponseHandler().loginResponseAccountAlreadyConnected();
                    break;
                case LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST:
                    DefinitionsManager.getLoginResponseHandler().loginResponseAccountDoesNotExist();
                    break;
                case LOGIN_FAILURE_INCORRECT_PASSWORD:
                    DefinitionsManager.getLoginResponseHandler().loginResponseIncorrectPassword();
                    break;
                case LOGIN_SUCCESS:
                    UserAccountManager.activeSession = lirid.USER_ACCOUNT;
                    DefinitionsManager.getLoginResponseHandler().loginResponseSuccess();
                    break;
                    default:
                        throw new IllegalStateException("Unhandled response code: " + lirid.RESPONSE_CODE);
            }
        } else if (instructionDatum instanceof LogOutInstructionDatum) {
            System.exit(0); //proper logout
        } else {
                //todo - more cases
                throw new IllegalArgumentException("Unhandled InstructionDatum class: " + instructionDatum.getClass());
        }
    }
}
