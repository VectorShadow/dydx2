package event;

import gamestate.gameobject.GameProjectile;
import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import main.LogHub;

import java.util.ArrayList;

public class ProjectileRotationEvent extends Event {

    private final GameProjectile PROJECTILE;
    private final boolean CLOCKWISE;

    public ProjectileRotationEvent(GameProjectile projectile, boolean clockwise) {
        super(Event.ExecutionOrder.IMMEDIATE);
        PROJECTILE = projectile;
        CLOCKWISE = clockwise;
    }

    @Override
    public ArrayList<GameZoneUpdate> execute() {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        try {
            updateList.add(
                    new GameZoneUpdate(
                            GameZone.class.getDeclaredMethod(
                                    "rotateProjectile",
                                    int.class,
                                    double.class
                            ),
                            PROJECTILE,
                            CLOCKWISE ? 0 - PROJECTILE.getTurningSpeed() : PROJECTILE.getTurningSpeed()
                    )
            );
        } catch (NoSuchMethodException e) {
            LogHub.logFatalCrash("No such method", e);
        }
        return updateList;
    }
}