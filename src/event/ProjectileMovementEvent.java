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
    public ArrayList<GameZoneUpdate> execute() {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        try {
            if (PROJECTILE.isDirect()) {
                //todo - handle disposal if travel() comes up short
                //todo - handle projectile interactions if they occur
                updateList.add(
                        new GameZoneUpdate(
                                GameZone.class.getDeclaredMethod(
                                        "moveProjectile",
                                        int.class,
                                        PointCoordinate.class
                                ),
                                PROJECTILE,
                                Pathfinder.travel(PROJECTILE, true)
                        )
                );
            }
            if (PROJECTILE.progress()) {
                if (!PROJECTILE.isDirect()) {
                    //todo - indirect projectiles land and must be resolved
                }
                updateList.add(
                        new GameZoneUpdate(
                                GameZone.class.getDeclaredMethod(
                                        "removeProjectile",
                                        GameProjectile.class
                                ),
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
