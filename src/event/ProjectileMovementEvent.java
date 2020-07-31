package event;

import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import gamestate.gameobject.GameProjectile;
import main.LogHub;

import java.util.ArrayList;

public class ProjectileMovementEvent extends Event {

    private final GameProjectile PROJECTILE;

    public ProjectileMovementEvent(GameProjectile projectile) {
        super(ExecutionOrder.PRIMARY);
        PROJECTILE = projectile;
    }

    @Override
    public ArrayList<GameZoneUpdate> execute() {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        try {
            if (PROJECTILE.isDirect()) {
                //todo - calculate the next point coordinate of this projectile.
                // If it reaches impassable terrain, dispose of it and return, otherwise update its position with setAt().
            }
            //todo - smart projectiles should have a way of changing their trajectory to track their target
            if (PROJECTILE.progress()) {
                if (!PROJECTILE.isDirect()) {
                    //todo - indirect projectiles land and must be resolved
                }
                updateList.add(
                        new GameZoneUpdate(
                                GameZone.class.getDeclaredMethod(
                                        "removeProjectile",
                                        GameProjectile.class),
                                PROJECTILE
                        )
                );
            }
        } catch (NoSuchMethodException e) {
            LogHub.logFatalCrash("No such method", e);
        }
        return updateList;
    }
}
