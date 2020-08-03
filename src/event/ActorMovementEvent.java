package event;

import ai.Pathfinder;
import gamestate.gamezone.GameZoneUpdate;
import gamestate.gameobject.GameActor;

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
        updateList.add(
                new GameZoneUpdate(
                        "moveActor",
                        ACTOR,
                        Pathfinder.travel(ACTOR, FORWARD)
                )
        );
        return updateList;
    }
}
