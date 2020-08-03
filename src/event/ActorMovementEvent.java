package event;

import ai.Pathfinder;
import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import gamestate.coordinates.PointCoordinate;
import gamestate.gameobject.GameActor;
import main.LogHub;

import java.util.ArrayList;

public class ActorMovementEvent extends Event {

    private final GameActor ACTOR;
    private final boolean FORWARD;

    public ActorMovementEvent(GameActor gameActor, boolean forward) {
        super(ExecutionOrder.SECONDARY);
        ACTOR = gameActor;
        FORWARD = forward;
    }

    @Override
    public ArrayList<GameZoneUpdate> execute() {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        try {
            updateList.add(
                    new GameZoneUpdate(
                            GameZone.class.getDeclaredMethod(
                                    "moveActor",
                                    int.class,
                                    PointCoordinate.class
                            ),
                            ACTOR,
                            Pathfinder.travel(ACTOR, FORWARD)
                    )
            );
        } catch (NoSuchMethodException e) {
            LogHub.logFatalCrash("No such method", e);
        }
        return updateList;
    }
}
