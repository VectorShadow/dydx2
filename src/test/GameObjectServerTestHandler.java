package test;

import gamestate.gameobject.GameObject;
import link.DataHandler;
import link.DataLink;
import link.instructions.InstructionDatum;

import java.net.Socket;

public class GameObjectServerTestHandler extends DataHandler {
    @Override
    protected void connectionLost(Socket socket) {

    }

    @Override
    protected void handle(int instructionCode, InstructionDatum instructionDatum, DataLink responseLink) {
        if (instructionCode == GameObjectTestInstruction.CODE) {
            GameObject go = ((GameObjectTestInstruction)instructionDatum).GAME_OBJECT;
            System.out.println("GameObject:\n" + go);
        }
        else System.out.println("Unrecognized code " + instructionCode);
    }
}
