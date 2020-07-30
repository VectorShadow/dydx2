package gamestate;

import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.TileCoordinate;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;
import gamestate.gameobject.SerialGameObject;
import gamestate.terrain.TerrainTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * GameZones represent towns, fields, dungeons, and other such areas of a game to which one or more players
 * may be connected, and on which all game activity occurs.
 */
public class GameZone {
    /**
     * Specify the size of the GameZone.
     */
    final int ROWS;
    final int COLUMNS;

    /**
     * Define the terrain within the GameZone.
     */
    final TerrainTile[][] terrain;

    /**
     * Provide an iterable reference to all actors in this zone.
     */
    final ArrayList<GameActor> actorList;
    /**
     * Provide a constant time reference to all actors in this Zone by their unique serial ID.
     * This is achieved by mapping the serial ID values to the actors using the hash function implemented in
     * SerialGameObject.
     * Note that actors are also tracked by each tile individually, and can be referenced by those tile coordinates
     * as well if that is preferable.
     */
    final Map actorMap;

    /**
     * Provide an iterable reference to all projectiles in this zone.
     */
    final ArrayList<GameProjectile> projectileList;
    /**
     * Provide a constant time reference to all projectiles in this Zone by their unique serial ID.
     * This is achieved by mapping the serial ID values to the projectiles using the hash function implemented in
     * SerialGameObject.
     * Note that projectiles, at least those travelling in a direct trajectory, rather than an indirect one, are
     * tracked by their coordinates, and can be accessed by the tile they are currently in, as well, if that is
     * preferable.
     */
    final Map projectileMap;

    GameZone(int height, int width) {
        ROWS = height;
        COLUMNS = width;
        terrain = new TerrainTile[ROWS][COLUMNS];
        actorList = new ArrayList<>();
        actorMap = Collections.synchronizedMap(new HashMap<Integer, GameActor>());
        projectileList = new ArrayList<>();
        projectileMap = Collections.synchronizedMap(new HashMap<Integer, GameProjectile>());
    }

    public ArrayList<GameActor> getActorList() {
        return actorList;
    }

    public Map getActorMap() {
        return actorMap;
    }

    public ArrayList<GameProjectile> getProjectileList() {
        return projectileList;
    }

    public Map getProjectileMap() {
        return projectileMap;
    }

    /**
     * Move the specified actor to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    public void moveActor(GameActor ga, PointCoordinate pc) {
        moveSerialGameObject(ga, pc);
    }
    /**
     * Move the specified projectile to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    public void moveProjectile(GameActor gp, PointCoordinate pc) {
        moveSerialGameObject(gp, pc);
    }
    private void moveSerialGameObject(SerialGameObject sgo, PointCoordinate pc) {
        TerrainTile source = tileAt(sgo.getAt().getParentTileCoordinate());
        TerrainTile destination = tileAt(pc.getParentTileCoordinate());
        boolean isProjectile = sgo instanceof GameProjectile;
        //indirect projectiles are not tracked by terrain tiles.
        if (isProjectile && !((GameProjectile)sgo).isDirect())
            return;
        ArrayList<SerialGameObject> sourceTracker = isProjectile ? source.projectileList : source.actorList;
        ArrayList<SerialGameObject> destinationTracker =
                isProjectile ? destination.projectileList : destination.actorList;
        if (source != destination) {
            if (!sourceTracker.remove(sgo))
                throw new IllegalStateException("Moved actor not found within its source tile tracker.");
            destinationTracker.add(sgo);
        }
        sgo.setAt(pc);
    }

    /**
     * Remove the specified actor from this gamezone, by first removing it from the tracker of the tile it
     * is in, then from the actor list, and finally from the actor map.
     * If it is not found in any of these places, throw an illegal state exception.
     */
    public void removeActor(GameActor ga) {
        if (!(tileAt(ga.getAt().getParentTileCoordinate()).actorList.remove(ga)))
            throw new IllegalStateException("Removed actor not found within its tile tracker.");
        if (!(actorList.remove(ga)))
            throw new IllegalStateException("Removed actor not found within the zone's actor list.");
        if (actorMap.remove(ga) == null)
            throw new IllegalStateException("Removed actor not found within the zone's actor map.");
    }

    /**
     * Remove the specified projectile from this gamezone, by first removing it from the tracker of the tile it
     * is in, then from the projectile list, and finally from the projectile map.
     * If it is not found in any of these places, throw an illegal state exception.
     */
    public void removeProjectile(GameProjectile gp) {
        //indirect projectiles are not tracked my terrain tiles
        if (gp.isDirect() && !(tileAt(gp.getAt().getParentTileCoordinate()).actorList.remove(gp)))
            throw new IllegalStateException("Removed actor not found within its tile tracker.");
        if (!(projectileList.remove(gp)))
            throw new IllegalStateException("Removed actor not found within the zone's actor list.");
        if (projectileMap.remove(gp) == null)
            throw new IllegalStateException("Removed actor not found within the zone's actor map.");
    }

    public TerrainTile tileAt(TileCoordinate tc) {
        return tileAt(tc.COLUMN, tc.ROW);
    }
    public TerrainTile tileAt(int column, int row) {
        return terrain[row][column];
    }

    private void invariant() {
        if (actorList.size() != actorMap.size())
            throw new IllegalStateException("Actor discrepancy - list has size " + actorList.size() +
                    "but map has size " + actorMap.size());
        if (projectileList.size() != projectileMap.size())
            throw new IllegalStateException("Projectile discrepancy - list has size " + projectileList.size() +
                    "but map has size " + projectileMap.size());
        //todo - more here?
    }
}
