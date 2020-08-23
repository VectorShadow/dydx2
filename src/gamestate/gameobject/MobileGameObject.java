package gamestate.gameobject;

import event.Event;
import gamestate.coordinates.PointCoordinate;

import java.util.ArrayList;

public abstract class MobileGameObject extends SerialGameObject {

    public static final double FACING_NORTH = 3.0 * Math.PI / 2.0;

    /**
     * This object's exact position in its current zone.
     */
    protected PointCoordinate at = null;

    /**
     * The current facing of this mobile object, in radians.
     */
    private double facing = FACING_NORTH; //default to north

    /**
     * @return the current point coordinate this mobile object is centered on.
     */
    public PointCoordinate getAt() {
        return at;
    }

    /**
     * @return the current facing of this mobile object in radians.
     */
    public double getFacing() {
        return facing;
    }

    /**
     * @return the movement access level of this mobile object. This must be either -1, or a value between 1 and 6.
     * (see TerrainProperties for more information)
     */
    public abstract int getMovementAccess();

    /**
     * @return the movement speed of this actor, or how much it can change its position in a turn.
     * The implementation is responsible for calculating a value for this.
     */
    public abstract int getMovementSpeed();

    /**
     * @return the turning speed of this actor, or fast it can change its facing in a turn.
     * The implementation is responsible for calculating a value for this.
     */
    public abstract double getTurningSpeed();

    /**
     * @return whether this mobile object follows material or energy rules of interaction.
     * This is determined by its movement access.
     */
    public boolean isMaterial() {
        return getMovementAccess() > 0;
    }

    /**
     * Adjust this mobile object's facing change by the specified amount.
     */
    public void rotate(double facingChange) {
        while (facingChange >= 2 * Math.PI)
            facingChange -= 2* Math.PI;
        while (facingChange <= 0 - (2 * Math.PI))
            facingChange += 2 * Math.PI;
        setFacing(getFacing() + facingChange);
    }

    /**
     * Set the current PointCoordinate this actor is centered on.
     */
    public void setAt(PointCoordinate at) {
        this.at = at;
    }

    /**
     * Set the facing of the mobile game object.
     */
    private void setFacing(double facing) {
        if (facing >= 2 * Math.PI)
            this.facing = facing - (2 * Math.PI);
        else if (facing < 0)
            this.facing = facing + (2 * Math.PI);
        else
            this.facing = facing;
    }

    /**
     * The engine will call this method for each mobile object at the start of every game turn.
     * The implementation must decide how to determine what events should be scheduled here.
     */
    public abstract ArrayList<Event> scheduleEvents();
}
