package link;

import link.instructions.InstructionDatum;

import java.net.Socket;

public class BackendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(InstructionDatum instructionDatum, DataLink responseLink) {
        //todo
    }
}
