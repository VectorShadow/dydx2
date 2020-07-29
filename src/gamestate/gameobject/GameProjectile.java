package gamestate.gameobject;

/**
 * Define a Projectile, which moves within or above the GameZone, but has no agency for generating events.
 * Implementations should extend this, defining fields according to their needs.
 */
public abstract class GameProjectile extends SerialGameObject {

    private static int serialCount = 0;

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }
}
