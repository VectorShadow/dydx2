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
