package test;

import link.DataHandler;
import link.DataLink;
import link.instructions.InstructionDatum;

import java.net.Socket;

public class GameObjectClientTestHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {

    }

    @Override
    protected void handle(int instructionCode, InstructionDatum instructionDatum, DataLink responseLink) {

    }
}
