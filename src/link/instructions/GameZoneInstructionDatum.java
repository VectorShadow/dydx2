package link.instructions;

import gamestate.gamezone.GameZone;

/**
 * Send an entire GameZone.
 */
public class GameZoneInstructionDatum extends InstructionDatum {
    public final GameZone GAME_ZONE;

    public GameZoneInstructionDatum(GameZone gz) {
        GAME_ZONE = gz;
    }
}
