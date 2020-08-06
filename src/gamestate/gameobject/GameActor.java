package gamestate.gameobject;

import event.ActorMovementEvent;
import event.ActorRotationEvent;
import event.Event;
import gamestate.order.MovementOrder;
import gamestate.order.RotationOrder;

import java.util.ArrayList;

/**
 * Define an Actor with Agency, capable of generating events within a GameZone.
 * Implementations should extend this, defining fields according to their needs.
 */
public abstract class GameActor extends MobileGameObject {

    protected MovementOrder movementOrder = null;
    protected RotationOrder rotationOrder = null;

    private static int serialCount = 1;

    @Override
    public boolean isMaterial() {
        return true; //all actors are made of matter
    }

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }

    @Override
    public ArrayList<Event> scheduleEvents() {
        ArrayList<Event> events = new ArrayList<>();
        if (movementOrder != null)
            events.add(new ActorMovementEvent(this, movementOrder.FORWARD));
        if (rotationOrder != null)
            events.add(new ActorRotationEvent(this, rotationOrder.CLOCKWISE));
        return events;
    }

    public void setMovementOrder(MovementOrder movementOrder) {
        this.movementOrder = movementOrder;
    }

    public void setRotationOrder(RotationOrder rotationOrder) {
        this.rotationOrder = rotationOrder;
    }

}
