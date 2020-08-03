package gamestate.gameobject;

import event.Event;

import java.util.ArrayList;

public abstract class MobileGameObject extends SerialGameObject {

    /**
     * The current facing of this mobile object, in radians.
     */
    private double facing;

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
     * Set the facing of the mobile game object.
     */
    private void setFacing(double facing) {
        if (facing > 2 * Math.PI)
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
