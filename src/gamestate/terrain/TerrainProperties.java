package gamestate.terrain;

import util.Direction;

/**
 * This class specifies the engine level properties of a terrain tile.
 * These are associated with the unique terrain ID field of the terrain tile, and must be defined
 * by the implementation.
 * Note that the implementation should also include drawing information and any other information it may need
 * to define a terrain tile which is not required by the engine.
 */
public abstract class TerrainProperties {

    /**
     * This value indicates that vision and energy projectiles will terminate on this tile. Both will still affect this
     * tile - it will be seen, and affected by the projectile if applicable.
     */
    public static final int ENERGY_PERMISSION_OPAQUE = 0;

    /**
     * This value indicates that vision and energy projectiles may pass through this terrain freely.
     */
    public static final int ENERGY_PERMISSION_TRANSPARENT = 1;

    /**
     * This value indicates that any entity capable of movement may pass this tile with no penalty.
     * Rolling and primitive walking actors should have this permission.
     */
    public static final int MATTER_PERMISSION_FREE = 0;
    
    /**
     * This value indicates that the terrain is uneven and may not be ideal for all movable entities.
     * Most walking actors should have this permission.
     */
    public static final int MATTER_PERMISSION_UNEVEN = 1;

    /**
     * This value indicates that the terrain contains obstacles to simple movement.
     * This might indicate a steep slope or river rapids which require special capabilities to traverse safely.
     * Climbing and jumping actors should have this permission.
     */
    public static final int MATTER_PERMISSION_OBSTACLE = 2;

    /**
     * This value indicates that the terrain can only be safely traversed by an actor which does not require 
     * gravitational traction with the ground to move. This might indicate a wall or chasm too steep or large
     * to be climbed or jumped over, and must be flown over.
     * Hovering or low-lying actors should have this permission.
     * All direct fire projectiles should have this permission.
     */
    public static final int MATTER_PERMISSION_SHEER = 3;

    /**
     * This value indicates that the terrain can only be traversed by very high flying actors and arcing projectiles.
     * High altitude actors and indirect fire projectiles should have this permission.
     */
    public static final int MATTER_PERMISSION_SKY = 4;
    
    /**
     * This value indicates that the terrain is a vacuum, and can only be traversed by entities specifically engineered
     * to survive there.
     * Space capable Actors and space based projectiles should have this permission.
     */
    public static final int MATTER_PERMISSION_VOID = 5;

    /**
     * This value indicates a solid terrain feature such as a wall that can only be passed through by insubstantial
     * actors or projectiles.
     * Ethereal beings and phased projectiles should have this permission.
     */
    public static final int MATTER_PERMISSION_ETHER = 6;

    /**
     * This value indicates a completely impassable terrain feature, such as a Zone boundary. No actor or projectile
     * may have movement permissions at this level.
     */
    public static final int MATTER_PERMISSION_IMPASSABLE = 7;

    /**
     * This value indicates a terrain feature which does not permit any sort of travel.
     */
    public static final int TRAVEL_PERMISSION_NONE = Direction.SELF.ordinal();

    /**
     * This value indicates a terrain feature which permits travel to a deeper level within the current location.
     */
    public static final int TRAVEL_PERMISSION_DOWN = Direction.SELF.ordinal() + 1;

    /**
     * This value indicates a terrain feature which permits travel to a shallower level within the current location.
     */
    public static final int TRAVEL_PERMISSION_UP = Direction.SELF.ordinal() + 2;

    /**
     * This value indicates how vision and energy based projectiles are affected upon reaching this terrain.
     */
    public final int ENERGY_PERMISSION;

    /**
     * This value indicates what kinds of movement may pass through this terrain and at what cost.
     * Actors with a movement access level greater than or equal to this value may pass through this terrain.
     * If their movement access level is lower than this value they may not enter this terrain.
     * Projectiles with a movement access level greater than or equal to this value may pass through this terrain.
     * If their movement access level is lower than this value, they will terminate their trajectories upon attempting
     * to enter this terrain.
     */
    public final int MATTER_PERMISSION;

    /**
     * This value indicates whether the terrain permits travel to a different Zone.
     * Values of 0-7 correspond to Direction value ordinals, used to change location ID.
     * A value of 8 corresponds to Direction.SELF, indicating no travel permission.
     * Values of 9 and 10 correspond to down and up travel permissions, used to change depth.
     */
    public final int TRAVEL_PERMISSION;
    
    public TerrainProperties(int energyPermission, int matterPermission, int travelPermission) {
        ENERGY_PERMISSION = energyPermission;
        MATTER_PERMISSION = matterPermission;
        TRAVEL_PERMISSION = travelPermission;
    }

    public Direction travelDirection() {
        if (TRAVEL_PERMISSION >= Direction.SELF.ordinal()) return null;
        for (Direction direction : Direction.values())
            if (direction.ordinal() == TRAVEL_PERMISSION) return direction;
        throw new IllegalStateException("Unhandeld Travel Permission value: " + TRAVEL_PERMISSION);
    }
}
