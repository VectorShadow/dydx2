package ai;

import definitions.DefinitionsManager;
import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.TileCoordinate;
import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;

import java.util.ArrayList;

//todo - good groundwork here, but probably needs a refactor based on MovementEvent redesign.
public class Pathfinder {
    /**
     * Find the point an actor moving to the targetTile will be at after one turn of movement.
     */
    public static PointCoordinate nextPoint(GameActor actor, TileCoordinate targetTile, GameZone zone) {
        int speed = actor.getMovementSpeed();
        PointCoordinate at = actor.getAt();
        PointCoordinate to = PointCoordinate.centerOf(targetTile);
        ArrayList<TileCoordinate> tilePath =
                calculateTilePath(
                        actor,
                        targetTile,
                        actor.getMovementAccess(),
                        zone
                );
        if (tilePath == null) return at; //if no path is found, return the actor's current location.
        PointCoordinate tempAt = at; //track where the actor is as we move
        int tileIndex = 0;
        int moveLeft = speed;
        do {
            //todo - draw a line of points tile by tile - look 2 ahead and draw a line from center to center, through
            // the intervening tile? is there a better way?
            //apply affects from the tile passed through here? does it even make sense to do that here? if not, where?
            ++tileIndex;
        } while (moveLeft > 0 && tileIndex < tilePath.size() - 1);
        return tempAt;
    }

    /**
     * Calculate a list of tiles which represent the shortest path from at to to in the specified zone.
     * Return that list, or null if no path can be found.
     */
    private static ArrayList<TileCoordinate> calculateTilePath(GameActor actor, TileCoordinate to, int access, GameZone zone) {
        ArrayList<TileCoordinate> shortestPath = shortestPath(actor.getAt().getParentTileCoordinate(), to);
        while ( //traverse the shortest path in reverse until the final tile permits the actor to move to it
                DefinitionsManager
                        .lookupTerrain()
                        .getMatterPermission(
                                zone.tileAt(
                                        shortestPath.get(
                                                shortestPath.size() - 1
                                        )
                                )
                        ) >= access
        ) {
            shortestPath.remove(shortestPath.size() - 1);
        }
        if (shortestPath.size() < 2) return null; //if one or zero tiles remain, no movement is possible.
        //todo - a lot here: find the shortest path of tiles whose movement permissions do not exceed tile access,
        // and which are currently not at or above their actor capacity. Ideally we should plan backwards, and simply
        // remove tiles at the end of the path which don't meet these criteria (so if the player sends a movement event
        // by clicking inside of a wall, the game will generate a move action in that direction). Ask the actor's AI
        // about damage aversion? Let the Actor's AI do this entirely?
        return null; //if no path can be found;
    }

    public static ArrayList<TileCoordinate> shortestPath(TileCoordinate tc1, TileCoordinate tc2) {
        //y = mx + b || x = (y - b) / m
        int x1 = tc1.COLUMN;
        int x2 = tc2.COLUMN;
        int y1 = tc1.ROW;
        int y2 = tc2.ROW;
        double m = (double)(y2 - y1) / (double)(x2 - x1);
        double b = (m * (double)y1) - (double)x1;
        boolean iterateX = m >= 1.0;
        ArrayList<TileCoordinate> path = new ArrayList<>();
        TileCoordinate next = tc1;
        while (!next.equals(tc2)) {
            path.add(next);
            next = iterateX ?
                    new TileCoordinate(next.COLUMN + 1, (int)((m * (next.COLUMN + 1)) + b)) :
                    new TileCoordinate((int)(((next.ROW + 1) - b) / m), next.ROW + 1);
        }
        path.add(next);
        return path;
    }
}
