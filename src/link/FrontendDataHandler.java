package link;

import gamestate.GameZone;
import gamestate.GameZoneUpdate;
import link.instructions.Codes;
import link.instructions.GameZoneUpdateInstructionDatum;
import link.instructions.InstructionDatum;

import java.net.Socket;

public class FrontendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(int instructionCode, InstructionDatum instructionDatum, DataLink responseLink) {
        switch (instructionCode) {
            case Codes.ENGINE_INSTRUCTION_CODE_TRANSMIT_GAME_ZONE_UPDATE:
                for (GameZoneUpdate gzu :((GameZoneUpdateInstructionDatum)instructionDatum).UPDATE_LIST)
                    GameZone.frontEnd.apply(gzu);
                //todo - send back a checksum
                break;
                //todo - more cases
            default:
                throw new IllegalArgumentException("Unhandled instruction code " + instructionCode);
        }
    }
}
