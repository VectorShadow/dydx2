package event;

import gamestate.GameZone;
import gamestate.GameZoneUpdate;
import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.TileCoordinate;
import gamestate.gameobject.GameActor;
import main.LogHub;

import java.util.ArrayList;

public class ActorMovementEvent extends Event {

    private final GameActor ACTOR;
    private final PointCoordinate DESTINATION;

    public ActorMovementEvent(GameActor gameActor, int movementSpeed, TileCoordinate targetTile) {
        super(ExecutionOrder.SECONDARY);
        ACTOR = gameActor;
        //todo - lots here, plan a path from gameActor's current PointCoordinate to the center of targetTile,
        // taking into account movement permissions, tile actor capacity, etc., and set DESTINATION to be a point
        // movementSpeed points along that path.
        DESTINATION = gameActor.getAt();
    }

    @Override
    public ArrayList<GameZoneUpdate> execute() {
        ArrayList<GameZoneUpdate> updateList = new ArrayList<>();
        try {
            updateList.add(
                    new GameZoneUpdate(
                            GameZone.class.getDeclaredMethod(
                                    "moveActor",
                                    GameActor.class,
                                    PointCoordinate.class
                            ),
                            ACTOR,
                            DESTINATION
                    )
            );
        } catch (NoSuchMethodException e) {
            LogHub.logFatalCrash("No such method", e);
        }
        return updateList;
    }
}
