package gamestate.gameobject;

import gamestate.coordinates.PointCoordinate;

/**
 * Used for GameObjects which are tracked via a unique SerialID.
 * In general, this is ideal when they have fields whose values may change at run time, for example,
 * Actors and Projectiles. Items and Features need not be mutable in this way, and should instead by entirely
 * specified by implementation definitions.
 */
public abstract class SerialGameObject extends GameObject {

    //provide a value for hashing serial game objects
    private static final int HASH_CONSTANT = 256;

    /**
     * This object's exact position in its current zone.
     */
    protected PointCoordinate at;

    protected final int serialID;

    public SerialGameObject() {
        serialID = nextSerialID();
    }

    public PointCoordinate getAt() {
        return at;
    }

    protected abstract int nextSerialID();

    public void setAt(PointCoordinate at) {
        this.at = at;
    }
    /**
     * Since serial game objects within a Zone are usually generated in quick succession, when the Zone is created,
     * a modulus based hash function provides the fewest collisions. As the number of objects grow beyond the value of
     * HASH_CONSTANT, or in situations where actors are added piecemeal after initial spawn, there may be more,
     * but this should provide the best overall performance for general cases.
     */
    @Override
    public int hashCode() {
        return HASH_CONSTANT % serialID;
    }
}
