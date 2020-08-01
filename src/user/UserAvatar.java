package user;

import gamestate.coordinates.ZoneCoordinate;

/**
 * This class specifies a user's avatar within the game world.
 * It should be extended by the implementation to provide implementation level details(Character class, etc.)
 */
public abstract class UserAvatar {
    private ZoneCoordinate at;

    public UserAvatar() {
        at = ZoneCoordinate.ORIGIN_ZONE;
    }

    public ZoneCoordinate getAt() {
        return at;
    }

    public void setAt(ZoneCoordinate at) {
        this.at = at;
    }
}