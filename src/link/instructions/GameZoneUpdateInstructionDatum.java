package link.instructions;

import gamestate.gamezone.GameZoneUpdate;

import java.util.ArrayList;

/**
 * This class wraps a list of GameStateUpdates to transfer on a link.
 */
public class GameZoneUpdateInstructionDatum extends InstructionDatum {

    public final int UPDATE_CHECKSUM;
    public final ArrayList<GameZoneUpdate> UPDATE_LIST;

    public GameZoneUpdateInstructionDatum(int checksum, ArrayList<GameZoneUpdate> updateList) {
        UPDATE_CHECKSUM = checksum;
        UPDATE_LIST = updateList;
    }
}
