package main;

import gamestate.GameZone;

/**
 * A sub-engine which processes all events for a particular zone.
 */
public class ZoneProcessor {

    private final GameZone GAME_ZONE;

    public ZoneProcessor(GameZone gz) {
        GAME_ZONE = gz;
    }

    /**
     * Process all events scheduled for this turn.
     */
    public void processTurn(){
        //todo
    }
}
