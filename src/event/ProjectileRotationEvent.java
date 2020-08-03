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
        updateList.add(
                new GameZoneUpdate(
                        "rotateProjectile",
                        PROJECTILE,
                        CLOCKWISE ? 0 - PROJECTILE.getTurningSpeed() : PROJECTILE.getTurningSpeed()
                )
        );
        return updateList;
    }
}