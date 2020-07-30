package event;

import gamestate.GameZone;
import gamestate.gameobject.GameProjectile;

public class ProjectileMovementEvent extends Event {

    private final GameProjectile PROJECTILE;

    public ProjectileMovementEvent(GameProjectile projectile) {
        super(ExecutionOrder.PRIMARY);
        PROJECTILE = projectile;
    }

    @Override
    public void execute(GameZone gz) {
        if (PROJECTILE.isDirect()) {
            //todo - calculate the next point coordinate of this projectile.
            // If it reaches impassable terrain, dispose of it and return, otherwise update its position with setAt().
        }
        //todo - smart projectiles should have a way of changing their trajectory to track their target.
        if (PROJECTILE.progress()) {
            if (!PROJECTILE.isDirect()) {
                //todo - indirect projectiles land and must be resolved
            }
            gz.removeProjectile(PROJECTILE);
        }
    }
}
