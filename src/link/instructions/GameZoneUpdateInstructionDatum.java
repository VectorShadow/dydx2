package link.instructions;

import gamestate.GameZoneUpdate;

import java.util.ArrayList;

/**
 * This class wraps a list of GameStateUpdates to transfer on a link.
 */
public class GameZoneUpdateInstructionDatum extends InstructionDatum {
    public final ArrayList<GameZoneUpdate> UPDATE_LIST;

    public GameZoneUpdateInstructionDatum(ArrayList<GameZoneUpdate> updateList) {
        UPDATE_LIST = updateList;
    }

    @Override
    protected int getInstructionCode() {
        return Codes.ENGINE_INSTRUCTION_CODE_TRANSMIT_GAME_ZONE_UPDATE;
    }
}
