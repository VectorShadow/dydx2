package gamestate.terrain;

/**
 * This class specifies the engine level properties of a terrain tile.
 * These are associated with the unique terrain ID field of the terrain tile, and must be defined
 * by the implementation.
 * Note that the implementation should also include drawing information and any other information it may need
 * to define a terrain tile which is not required by the engine.
 */
public abstract class TerrainProperties {

    /**
     * This value indicates that vision and energy projectiles may pass through this terrain freely.
     */
    public static final int ENERGY_PERMISSION_TRANSPARENT = 0;

    /**
     * This value indicates that vision terminates on this tile, while energy projectile damage and penetration is
     * reduced by half when passing through this terrain. This is multiplicative - if an energy based projectile passes
     * through two translucent tiles, it will have its damage and penetration quartered.
     */
    public static final int ENERGY_PERMISSION_TRANSLUCENT = 0;

    /**
     * This value indicates that vision and energy projectiles will terminate on this tile. Both will still affect this
     * tile - it will be seen, and affected by the projectile if applicable.
     */
    public static final int ENERGY_PERMISSION_OPAQUE = 0;

    /**
     * This value indicates that any entity capable of movement may pass this tile with no penalty.
     * No actor may have a movement permission this low.
     */
    public static final int MATTER_PERMISSION_FREE = 0;
    
    /**
     * This value indicates that the terrain is rough and may not be ideal for all movable entities.
     * Rolling and primitive walking actors should have this permission.
     */
    public static final int MATTER_PERMISSION_ROUGH = 1;

    /**
     * This value indicates that the terrain is perilous to traverse. This might indicate a steep slope or river rapids
     * which require special capabilities to traverse safely.
     * Most walking actors should have this permission.
     */
    public static final int MATTER_PERMISSION_PERILOUS = 2;

    /**
     * This value indicates that the terrain can only be safely traversed by an actor which does not require 
     * gravitational traction with the ground to move. This might indicate a low wall or small chasm which can be
     * scaled or jumped by a climbing actor if necessary, but otherwise must be flown over.
     * Climbing and jumping actors should have this permission.
     */
    public static final int MATTER_PERMISSION_AIRBORNE = 3;

    /**
     * This value indicates that the terrain can only be traversed by very high flying actors and projectiles.
     * Flying actors should have this permission.
     * All direct fire projectiles should have this permission.
     */
    public static final int MATTER_PERMISSION_ORIBITAL = 4;
    
    /**
     * This value indicates that the terrain is a void in space and can only be safely traversed by 
     * space capable actors or projectiles.
     * Space capable actors should have this permission.
     * Indirect fire projectiles and deep space munitions should have this permission.
     */
    public static final int MATTER_PERMISSION_VOID = 5;

    /**
     * This value indicates a solid terrain feature such as a wall that can only be passed through by insubstantial
     * actors or projectiles.
     * Ethereal beings should have this permission.
     * Phased projectiles should have this permission.
     */
    public static final int MATTER_PERMISSION_ETHEREAL = 6;

    /**
     * This value indicates a completely impassable terrain feature, such as a Zone boundary. No actor may have movement
     * permissions at this level.
     */
    public static final int MATTER_PERMISSION_IMPASSABLE = 7;

    /**
     * This value indicates how vision and energy based projectiles are affected upon reaching this terrain.
     */
    public final int ENERGY_PERMISSION;

    /**
     * This value indicates what kinds of movement may pass through this terrain and at what cost.
     * Any actor with a movement value greater than this value may pass the terrain at no penalty.
     * Any actor with a movement value equal to this value may pass through the terrain, but may not stop
     * there.
     * Any actor with a movement value less than this value may not pass through the terrain.
     * Any projectile with movement value greater than or equal to this value may advance along a trajectory
     * through this terrain.
     * Any projectile with movement value less than this value will terminate its trajectory upon reaching this terrain.
     */
    public final int MATTER_PERMISSION;
    
    public TerrainProperties(int energyPermission, int matterPermission) {
        ENERGY_PERMISSION = energyPermission;
        MATTER_PERMISSION = matterPermission;
    }
}
