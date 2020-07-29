package gamestate.gameobject;

import event.Event;

import java.util.ArrayList;

/**
 * Define an Actor with Agency, capable of generating events within a GameZone.
 * Implementations should extend this, defining fields according to their needs.
 */
public abstract class GameActor extends SerialGameObject {

    private static int serialCount = 0;

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }

    /**
     * The engine will call this method for each actor at the start of every game turn.
     * The implementation must decide how to determine what events should be scheduled here.
     */
    public abstract ArrayList<Event> scheduleEvents();
}
