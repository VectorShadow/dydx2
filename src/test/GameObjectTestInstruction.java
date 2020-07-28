package test;

import gamestate.GameObject;
import link.instructions.InstructionDatum;

public class GameObjectTestInstruction extends InstructionDatum {
    public static final int CODE = InstructionDatum.FIRST_ENCRYPTABLE_INSTRUCTION_CODE;

    public final GameObject GAME_OBJECT;

    public GameObjectTestInstruction(GameObject go) {
        GAME_OBJECT = go;
    }

    @Override
    protected int getInstructionCode() {
        return CODE;
    }
}
