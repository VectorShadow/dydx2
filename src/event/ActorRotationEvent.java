package event;

import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import main.LogHub;

import java.util.ArrayList;

public class ActorRotationEvent extends Event {

    private final GameActor ACTOR;
    private final boolean CLOCKWISE;

    public ActorRotationEvent(GameActor actor, boolean clockwise) {
        super(ExecutionOrder.IMMEDIATE);
        ACTOR = actor;
        CLOCKWISE = clockwise;
    }

    @Override
    public ArrayList<GameZoneUpdate> execute() {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        updateList.add(
                new GameZoneUpdate(
                        "rotateActor",
                        ACTOR,
                        CLOCKWISE ? 0 - ACTOR.getTurningSpeed() : ACTOR.getTurningSpeed()
                )
        );
        return updateList;
    }
}