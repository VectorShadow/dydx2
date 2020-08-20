package user;

import gamestate.TransmittableGameAsset;
import gamestate.coordinates.ZoneCoordinate;

public abstract class AvatarMetadata extends TransmittableGameAsset {

    protected final ZoneCoordinate ZONE_COORDINATE;

    protected AvatarMetadata(ZoneCoordinate zoneCoordinate) {
        ZONE_COORDINATE = zoneCoordinate;
    }

    public ZoneCoordinate getZoneCoordinate() {
        return ZONE_COORDINATE;
    }

    @Override
    public abstract String toString();
}
