package gamestate.gameobject;

import gamestate.terrain.TerrainProperties;

/**
 * Define a Projectile, which moves within or above the GameZone, but has no agency for generating events.
 * Implementations should extend this, defining fields according to their needs.
 */
public abstract class GameProjectile extends MobileGameObject {

    private static int serialCount = 1;

    /**
     * Specifies whether this projectile travels in a direct trajectory, or in an indirect arc over the top
     * of the game zone.
     */
    private final boolean DIRECT;

    /**
     * Specifies the movement access of this projectile.
     * This must be either -1, indicating an immaterial projectile which interacts with terrain energy permissions,
     * or 4, 5, or 6, indicating a material projectile which interacts with terrain matter permissions.
     */
    private final int MOVEMENT_ACCESS;

    /**
     * The current speed at which this projectile is travelling, in meters per game turn.
     */
    private final int SPEED;

    /**
     * Current and Maximum ranges specify how far the projectile has travelled, and how far it can travel before
     * terminating its trajectory. When a direct projectile reaches or exceeds its maximum range, it drops to the
     * ground and ceases to exist. When an indirect projectile does so, this signifies impact, and the interaction
     * with whatever is at its destination must resolve.
     */
    private int currentRange = 0;
    private final int MAX_RANGE;

    public GameProjectile(boolean isDirect, int maxRange, int movementAccess, int speed) {
        DIRECT = isDirect;
        MAX_RANGE = maxRange;
        if (movementAccess >= 0 &&
                (movementAccess < TerrainProperties.MATTER_PERMISSION_SHEER
                        || movementAccess > TerrainProperties.MATTER_PERMISSION_ETHER))
            throw new IllegalArgumentException("Invalid movement access, must be -1, 4, 5, or 6 (was "
                    + movementAccess + ").");
        MOVEMENT_ACCESS = movementAccess;
        SPEED = speed;
    }

    @Override
    public int getMovementSpeed() {
        return SPEED;
    }

    public boolean isDirect() {
        return DIRECT;
    }

    /**
     * Move this projectile along its trajectory.
     * @return whether it has reached or exceeded its maximum range.
     */
    public boolean progress() {
        return (currentRange += SPEED) >= MAX_RANGE;
    }

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }
}
