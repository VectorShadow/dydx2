package main;

import event.Event;
import event.ProjectileMovementEvent;
import gamestate.GameZone;
import gamestate.GameZoneUpdate;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;
import link.instructions.GameZoneUpdateInstructionDatum;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A sub-engine which processes all events for a particular zone.
 */
public class ZoneProcessor {

    private final GameZone GAME_ZONE;
    private final ZoneProcessorNode PARENT_NODE;

    public ZoneProcessor(GameZone gz, ZoneProcessorNode pn) {
        GAME_ZONE = gz;
        PARENT_NODE = pn;
    }

    /**
     * Process all events scheduled for this turn.
     */
    void processTurn(){
        ArrayList<Event> eventQueue = new ArrayList<>();
        //request events from all actors
        for (GameActor ga : GAME_ZONE.getActorList())
            eventQueue.addAll(ga.scheduleEvents());
        //schedule trajectory progress for all projectiles
        for (GameProjectile gp : GAME_ZONE.getProjectileList())
            eventQueue.add(new ProjectileMovementEvent(gp));
        //sort events by execution order
        eventQueue.sort(Comparator.naturalOrder());
        ArrayList<GameZoneUpdate> eventUpdates;
        ArrayList<GameZoneUpdate> turnUpdates = new ArrayList<>();
        for (Event e : eventQueue) {
            //get all updates associated with each event
            eventUpdates = e.execute();
            //collect all updates for transmission, then apply them to this processor's GameZone
            for (GameZoneUpdate gzu : eventUpdates) {
                turnUpdates.add(gzu);
                GAME_ZONE.apply(gzu);
            }
        }
        //transmit all updates on all data links connected to this zone processor
        for (DataLinkNode dln : PARENT_NODE.LINKS)
            dln.LINK.transmit(new GameZoneUpdateInstructionDatum(turnUpdates));
    }
}
