package event;

import ai.Pathfinder;
import gamestate.gamezone.GameZone;
import gamestate.gamezone.GameZoneUpdate;
import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.TileCoordinate;
import gamestate.gameobject.GameActor;
import main.LogHub;

import java.util.ArrayList;

public class ActorMovementEvent extends Event {

    private final GameActor ACTOR;
    private final PointCoordinate DESTINATION;

    public ActorMovementEvent(GameActor gameActor, TileCoordinate targetTile, GameZone gameZone) {
        super(ExecutionOrder.SECONDARY);
        ACTOR = gameActor;
        //todo - this is not how this should work. Instead, let the actor interpret its active move orders
        // to find its own path, then schedule a movement event based on that path when the zone processor
        // requests events from that actor. The event should then contain a list of all tiles the actor will pass
        // through, as well as its final destination. All we need to do here is get any effects associated with the
        // tiles we pass through, such as stoppage, slowing, environmental conditions like fire, water, shock,
        // whatever else, and add ensure we add gamezoneupdates appropriate for each effect on the way, also changing
        // the destination to the first tile that caused a stoppage(and ignoring any effects on subsequent tiles).
        DESTINATION = Pathfinder.nextPoint(ACTOR, targetTile, gameZone);
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
