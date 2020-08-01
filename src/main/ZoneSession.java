package main;

import event.Event;
import event.ProjectileMovementEvent;
import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import gamestate.coordinates.ZoneCoordinate;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;
import link.instructions.GameZoneUpdateInstructionDatum;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Track a ZoneProcessor and all DataLinks currently connected to it.
 */
public class ZoneSession {

    /**
     * A sub-engine which processes all events for a particular zone.
     */
    private class ZoneProcessor {

        private final GameZone GAME_ZONE;

        private ZoneProcessor(GameZone gz) {
            GAME_ZONE = gz;
        }

        /**
         * Process all events scheduled for this turn.
         */
        private void processTurn(ArrayList<DataLinkSession> linkNodes){
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
            for (DataLinkSession dln : linkNodes)
                dln.LINK.transmit(new GameZoneUpdateInstructionDatum(GAME_ZONE.getCheckSum(), turnUpdates));
        }
    }
    final ZoneCoordinate COORD;
    final ArrayList<DataLinkSession> LINKS;
    private final ZoneProcessor PROCESSOR;

    ZoneSession(ZoneCoordinate zc, GameZone gz) {
        COORD = zc;
        LINKS = new ArrayList<>();
        PROCESSOR = new ZoneProcessor(gz);
    }

    void processTurn() {
        PROCESSOR.processTurn(LINKS);
    }

    GameZone getGameZone() {
        return PROCESSOR.GAME_ZONE;
    }
}
