package gamestate.coordinates;

import gamestate.TransmittableGameAsset;

/**
 * Specify the world location of a GameZone.
 */
public class ZoneCoordinate extends TransmittableGameAsset implements Comparable<ZoneCoordinate> {

    /**
     * The unique location id of the origin zone.
     */
    public static final int ORIGIN_ZONE_ID = 0;

    /**
     * The depth of a surface level Zone. Most towns and fields should have this depth.
     */
    public static final int SURFACE_DEPTH = 0;

    /**
     * The global instance of a Zone.
     */
    public static final int GLOBAL_INSTANCE = -1;

    /**
     * The full coordinate of the origin zone.
     */
    public static final ZoneCoordinate ORIGIN_ZONE = new ZoneCoordinate(ORIGIN_ZONE_ID, SURFACE_DEPTH, GLOBAL_INSTANCE);

    /**
     * The unique world location identifier.
     * Each identifier corresponds to a specific location within the world.
     */
    public final int LOCATION_ID;

    /**
     * The depth of this Zone within its world location.
     * Some world locations, such as caves or dungeons, support multiple levels, each of which must be unique.
     */
    public final int DEPTH;

    /**
     * The instance index of this Zone. Generally players will use the global instance, but if a player needs a unique
     * instance of a Zone, for quests or solo play or group missions, an additional Zone at the same location and depth
     * will be created with the next available instance number.
     */
    public final int INSTANCE;

    public ZoneCoordinate(int locationId, int depth, int instance) {
        LOCATION_ID = locationId;
        DEPTH = depth;
        INSTANCE = instance;
    }

    /**
     * Compare first by location ID, then by depth, and finally by instance.
     */
    @Override
    public int compareTo(ZoneCoordinate zc) {
        return LOCATION_ID == zc.LOCATION_ID ?
                DEPTH == zc.DEPTH ?
                INSTANCE - zc.INSTANCE :
                DEPTH - zc.DEPTH :
                LOCATION_ID - zc.LOCATION_ID;
    }

    /**
     * ZoneCoordinates are equal if and only if they have the exact same location id, depth, and instance.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof ZoneCoordinate && compareTo((ZoneCoordinate)o) == 0;
    }

    @Override
    public String toString() {
        return "ZoneCoordinate[L: " + LOCATION_ID + "/D:" + DEPTH + "/I:" + INSTANCE + "]";
    }
}
