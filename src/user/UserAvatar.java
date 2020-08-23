package user;

import gamestate.coordinates.PointCoordinate;
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

    private PointCoordinate lastActorLocation = null;

    private ZoneKnowledge zoneKnowledge = null;

    /**
     * Generate an AvatarMetadata object based on this avatar.
     */
    abstract AvatarMetadata buildMetadata();

    /**
     * Dynamically generate an actor, or select a pre-generated actor, based on implementation.
     */
    protected abstract void createActor();

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

    public ZoneCoordinate getAt() {
        return at;
    }

    public ZoneKnowledge getZoneKnowledge() {
        return zoneKnowledge;
    }

    /**
     * Restore the current actor's location once we log back in.
     */
    public void restoreLastActorLocation() {
        if (actor != null)
            actor.setAt(lastActorLocation);
    }

    /**
     * Backup the current actor's location, before it is removed from a zone and cleared on logout.
     */
    public void saveLastActorLocation() {
        if (actor != null)
            lastActorLocation = actor.getAt();
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
