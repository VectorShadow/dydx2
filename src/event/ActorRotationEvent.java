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
        try {
            updateList.add(
                    new GameZoneUpdate(
                            GameZone.class.getDeclaredMethod(
                                    "rotateActor",
                                    int.class,
                                    double.class
                            ),
                            ACTOR,
                            CLOCKWISE ? 0 - ACTOR.getTurningSpeed() : ACTOR.getTurningSpeed()
                    )
            );
        } catch (NoSuchMethodException e) {
            LogHub.logFatalCrash("No such method", e);
        }
        return updateList;
    }
}
