package gamestate.gameobject;

/**
 * Define a Projectile, which moves within or above the GameZone, but has no agency for generating events.
 */
public class GameProjectile extends GameObject {
    @Override
    protected Object[] declareFields() {
        return new Object[0];
    }
}
