package link;

import link.instructions.InstructionDatum;

import java.net.Socket;

public class FrontendDataHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {
        //todo
    }

    @Override
    protected void handle(int instructionCode, InstructionDatum instructionDatum, DataLink responseLink) {
        //todo
    }
}
