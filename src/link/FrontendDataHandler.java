package link;

import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import link.instructions.GameZoneUpdateInstructionDatum;
import link.instructions.InstructionDatum;

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
        } else {
                //todo - more cases
                throw new IllegalArgumentException("Unhandled InstructionDatum class: " + instructionDatum.getClass());
        }
    }
}
