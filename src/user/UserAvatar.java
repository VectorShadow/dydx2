package user;

import gamestate.coordinates.ZoneCoordinate;
import gamestate.gameobject.GameActor;

import java.io.Serializable;

/**
 * This class specifies a user's avatar within the game world.
 * It should be extended by the implementation to provide implementation level details(Character class, etc.)
 */
public abstract class UserAvatar implements Serializable {

    private GameActor actor;

    private ZoneCoordinate at;

    public UserAvatar() {
        at = ZoneCoordinate.ORIGIN_ZONE;
    }

    /**
     * Construct an appropriate Actor for this Avatar on the engine side(so it conforms to serial ID invariant).
     * This method first calls deriveActor(), which is an implementation specific method for creating an actor
     * to represent this Avatar in its GameZone. It then sets its own actor with the returned value, and returns it
     * for external usage.
     */
    public GameActor constructActor() {
        setActor(deriveActor());
        return getActor();
    }

    /**
     * Implementation specific method for deriving an Actor to represent this Avatar. This *must* construct a new
     * GameActor object, whether by building one from stored values or cloning a stored value, else it will break the
     * serialID invariant.
     */
    protected abstract GameActor deriveActor();

    public GameActor getActor() {
        return actor;
    }

    public ZoneCoordinate getAt() {
        return at;
    }

    public void setActor(GameActor actor) {
        this.actor = actor;
    }

    public void setAt(ZoneCoordinate at) {
        this.at = at;
    }
}
