package main;

import event.Event;
import gamestate.GameZone;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;

import java.util.ArrayList;
import java.util.Comparator;

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
    void processTurn(){
        ArrayList<Event> eventQueue = new ArrayList<>();
        for (GameActor ga : GAME_ZONE.getActorList())
            eventQueue.addAll(ga.scheduleEvents());
        for (GameProjectile ga : GAME_ZONE.getProjectileList())
            ;
        eventQueue.sort(Comparator.naturalOrder());
        for (Event e : eventQueue)
            e.execute(GAME_ZONE);
    }
}
