package event;

import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;

import java.util.ArrayList;

/**
 * An event which changes the gamestate.
 */
//todo - this may need a complete rework!
public abstract class Event implements Comparable<Event> {
    public enum ExecutionOrder {
        IMMEDIATE,
        PRIMARY,
        SECONDARY,
        FINAL
    }

    public final ExecutionOrder XO;

    public Event(ExecutionOrder xo) {
        XO = xo;
    }

    /**
     * Sort events by ExecutionOrder.
     * Events of the same ExecutionOrder need not be sorted further, as their execution in any order must result in
     * the same final gamestate.
     */
    @Override
    public int compareTo(Event e) {
        return XO.compareTo(e.XO);
    }

    public abstract ArrayList<GameZoneUpdate> execute(GameZone gameZone);

}
