package event;

import ai.Pathfinder;
import gamestate.coordinates.PointCoordinate;
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
    public ArrayList<GameZoneUpdate> execute(GameZone gameZone) {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        if (PROJECTILE.isDirect()) {
            //todo - handle disposal if travel() comes up short
            //todo - handle projectile interactions if they occur
            updateList.add(
                    new GameZoneUpdate(
                            "moveProjectile",
                            PROJECTILE.getSerialID(),
                            Pathfinder.travel(gameZone, PROJECTILE, true)
                    )
            );
        }
        if (PROJECTILE.progress()) {
            if (!PROJECTILE.isDirect()) {
                //todo - indirect projectiles land and must be resolved
            }
            updateList.add(
                    new GameZoneUpdate(
                            "removeProjectile",
                            PROJECTILE.getSerialID()
                    )
            );
        }
        return updateList;
    }
}
