package user;

import gamestate.coordinates.ZoneCoordinate;
import gamestate.gameobject.GameActor;

import java.io.Serializable;

/**
 * This class specifies a user's avatar within the game world.
 * It should be extended by the implementation to provide implementation level details(Character class, etc.)
 */
public abstract class UserAvatar implements Serializable {

    protected GameActor actor = null;

    protected ZoneCoordinate at = ZoneCoordinate.ORIGIN_ZONE;

    //todo - saving this causes EOF errors somehow(probably related to GameZone - similar issues when actors carried
    // that field). Look into!
    private ZoneKnowledge zoneKnowledge = null;

    /**
     * Generate an AvatarMetadata object based on this avatar.
     */
    abstract AvatarMetadata buildMetadata();

    /**
     * Get the current actor in use by this Avatar.
     * If the current actor is null, call the implementation's createActor method, which will dynamically generate
     * or select a pre-generated Actor.
     */
    public GameActor getActor() {
        if (actor == null)
            createActor();
        return actor;
    }

    /**
     * Dynamically generate an actor, or select a pre-generated actor, based on implementation.
     */
    protected abstract void createActor();

    public ZoneCoordinate getAt() {
        return at;
    }

    public ZoneKnowledge getZoneKnowledge() {
        return zoneKnowledge;
    }

    public void setActor(GameActor actor) {
        this.actor = actor;
    }

    public void setAt(ZoneCoordinate at) {
        this.at = at;
    }

    public void setZoneKnowledge(ZoneKnowledge zk) {
        zoneKnowledge = zk;
    }
}
