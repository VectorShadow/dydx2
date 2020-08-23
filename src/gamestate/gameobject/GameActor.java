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

    private static int serialCount = 1;

    protected MovementOrder movementOrder = null;
    protected RotationOrder rotationOrder = null;

    private int travelFlag = -1;

    public int getTravelFlag() {
        return travelFlag;
    }

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

    /**
     * Return whether this method changed the current order.
     */
    public boolean setMovementOrder(MovementOrder mo) {
        if (
                (movementOrder == null && mo == null) ||
                        movementOrder != null && movementOrder.equals(mo)
        )
            return false;
        movementOrder = mo;
        return true;
    }

    /**
     * Return whether this method changed the current order.
     */
    public boolean setRotationOrder(RotationOrder ro) {
        if (
                (rotationOrder == null && ro == null) ||
                        rotationOrder != null && rotationOrder.equals(ro)
        )
            return false;
        rotationOrder = ro;
        return true;
    }

    public void setTravelFlag(int outboundTravelPermission) {
        travelFlag = outboundTravelPermission;
    }

}
