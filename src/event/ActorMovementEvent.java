package event;

import ai.Pathfinder;
import gamestate.gamezone.GameZone;
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
    public ArrayList<GameZoneUpdate> execute(GameZone gameZone) {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        updateList.add(
                new GameZoneUpdate(
                        "moveActor",
                        ACTOR.getSerialID(),
                        Pathfinder.travel(gameZone, ACTOR, FORWARD)
                )
        );
        return updateList;
    }
}
