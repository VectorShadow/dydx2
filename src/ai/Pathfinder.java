package ai;

import definitions.DefinitionsManager;
import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.Coordinate;
import gamestate.gameobject.MobileGameObject;
import gamestate.gamezone.GameZone;
import gamestate.terrain.TerrainTile;

import java.util.ArrayList;

/**
 * Provide basic pathfinding functions.
 */
public class Pathfinder {

    private static ArrayList<Coordinate> shortestPath(Coordinate tc1, Coordinate tc2) {
        //y = mx + b || x = (y - b) / m || b = y - mx
        int x1 = tc1.COLUMN;
        int x2 = tc2.COLUMN;
        int y1 = tc1.ROW;
        int y2 = tc2.ROW;
        int dx = x2 - x1;
        int dy = y2 - y1;
        double m = (double)dy / (double)dx;
        double b = (double)y1 - (m * (double)x1);
        boolean iterateX = m >= 1.0;
        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;
        boolean slopeZero = dy == 0;
        boolean slopeInf = dx == 0;
        ArrayList<Coordinate> path = new ArrayList<>();
        Coordinate next = tc1;
        while (!next.equals(tc2)) {
            path.add(next);
            next = slopeZero ?
                    new Coordinate(next.COLUMN + stepX, next.ROW)
                    : slopeInf ?
                    new Coordinate(next.COLUMN, next.ROW + stepY)
                    : iterateX ?
                    new Coordinate(
                            next.COLUMN + stepX,
                            (int)Math.round((m * (next.COLUMN + stepX)) + b))
                    : new Coordinate((int)Math.round(((next.ROW + stepY) - b) / m), next.ROW + stepY);
        }
        path.add(next);
        return path;
    }

    private static ArrayList<Coordinate> trajectory(Coordinate origin, int distance, double facing) {
        double dx = (double)distance * Math.cos(facing);
        double dy = (double)distance * Math.sin(facing);
        Coordinate destination = new Coordinate((int)Math.round(origin.COLUMN + dx), (int)Math.round(origin.ROW + dy));
        return shortestPath(origin, destination);
    }

    /**
     * Cause a MobileGameObject to travel along its current trajectory as far as possible.
     * We do this by getting the MGO's current position as a point coordinate, facing in radians,
     * and speed in meters per turn(as they are now in this phase of execution, taking into account updates
     * that may have been applied in previous phases, such as facing turns). From these values, we calculate a
     * trajectory to the ideal final tile.
     * For each point along this trajectory, we find the parent tile in the MGO's game zone and check its movement
     * permission against the MGO's movement access. If the access is sufficient, we progress the actor along the
     * trajectory - if not, we end the move.
     */
    //todo - this method must convey whether an MGO was stopped early. What we should probably do is return the
    // planned path, then the actual path. If the actual path is shorter than the planned path, we know we had a
    // premature stop. Further, we can calculate projectile interactions along the actual path.

    //todo - we need to adapt this method to handle energetic projectiles eventually, and if so we need to convey
    // any reduction that results from passing through translucent tiles.
    public static PointCoordinate travel(GameZone gameZone, MobileGameObject mgo, boolean forward) {
        PointCoordinate at = mgo.getAt();
        double direction = mgo.getFacing();
        int speed = mgo.getMovementSpeed();
        ArrayList<Coordinate> pathPoints =
                trajectory(
                        at,
                        speed,
                        forward ?
                                direction
                                : direction > Math.PI ?
                                direction - Math.PI
                                : direction + Math.PI
                );
        PointCoordinate pathPoint;
        TerrainTile terrainTile;
        for (int i = 1; i < pathPoints.size(); ++i) {
            //interpret the trajectory coordinates as point coordinates so we can access their parent tile coordinates
            pathPoint = new PointCoordinate(pathPoints.get(i));
            terrainTile = gameZone.tileAt(pathPoint.getParentTileCoordinate());
            if (DefinitionsManager.getTerrainLookup().checkAccess(mgo, terrainTile))
                at = pathPoint;
            else break;
        }
        return at;
    }
}
