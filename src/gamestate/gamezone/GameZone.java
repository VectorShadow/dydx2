package gamestate.gamezone;

import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.TileCoordinate;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;
import gamestate.gameobject.SerialGameObject;
import gamestate.terrain.TerrainTile;
import main.LogHub;

import java.lang.reflect.InvocationTargetException;
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
     * This is the game zone presented to the front end.
     * The FrontEndDataHandler will keep this updated.
     * The implementation will need access to it to present a graphical representation to the user.
     */
    public static GameZone frontEnd = null;

    /**
     * Specify the size of the GameZone.
     */
    final int ROWS;
    final int COLUMNS;

    /**
     * Define the terrain within the GameZone.
     */
    final TerrainTile[][] TERRAIN;

    /**
     * Provide an iterable reference to all actors in this zone.
     */
    final ArrayList<GameActor> ACTOR_LIST;
    /**
     * Provide a constant time reference to all actors in this Zone by their unique serial ID.
     * This is achieved by mapping the serial ID values to the actors using the hash function implemented in
     * SerialGameObject.
     * Note that actors are also tracked by each tile individually, and can be referenced by those tile coordinates
     * as well if that is preferable.
     */
    final Map ACTOR_MAP;

    /**
     * Provide an iterable reference to all projectiles in this zone.
     */
    final ArrayList<GameProjectile> PROJECTILE_LIST;
    /**
     * Provide a constant time reference to all projectiles in this Zone by their unique serial ID.
     * This is achieved by mapping the serial ID values to the projectiles using the hash function implemented in
     * SerialGameObject.
     * Note that projectiles, at least those travelling in a direct trajectory, rather than an indirect one, are
     * tracked by their coordinates, and can be accessed by the tile they are currently in, as well, if that is
     * preferable.
     */
    final Map PROJECTILE_MAP;

    GameZone(int height, int width) {
        ROWS = height;
        COLUMNS = width;
        TERRAIN = new TerrainTile[ROWS][COLUMNS];
        ACTOR_LIST = new ArrayList<>();
        ACTOR_MAP = Collections.synchronizedMap(new HashMap<Integer, GameActor>());
        PROJECTILE_LIST = new ArrayList<>();
        PROJECTILE_MAP = Collections.synchronizedMap(new HashMap<Integer, GameProjectile>());
    }

    private GameActor lookupActor(int serialID) {
        return (GameActor) ACTOR_MAP.get(serialID);
    }

    private GameProjectile lookupProjectile(int serialID) {
        return (GameProjectile) PROJECTILE_MAP.get(serialID);
    }

    public ArrayList<GameActor> getActorList() {
        return ACTOR_LIST;
    }

    public Map getActorMap() {
        return ACTOR_MAP;
    }

    public ArrayList<GameProjectile> getProjectileList() {
        return PROJECTILE_LIST;
    }

    public Map getProjectileMap() {
        return PROJECTILE_MAP;
    }

    /**
     * Move the specified actor to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    public void moveActor(int serialID, PointCoordinate pc) {
        moveSerialGameObject(lookupActor(serialID), pc);
    }
    /**
     * Move the specified projectile to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    public void moveProjectile(int serialID, PointCoordinate pc) {
        moveSerialGameObject(lookupProjectile(serialID), pc);
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
    public void removeActor(int serialID) {
        GameActor ga = lookupActor(serialID);
        if (!(tileAt(ga.getAt().getParentTileCoordinate()).actorList.remove(ga)))
            throw new IllegalStateException("Removed actor not found within its tile tracker.");
        if (!(ACTOR_LIST.remove(ga)))
            throw new IllegalStateException("Removed actor not found within the zone's actor list.");
        if (ACTOR_MAP.remove(ga) == null)
            throw new IllegalStateException("Removed actor not found within the zone's actor map.");
    }

    /**
     * Remove the specified projectile from this gamezone, by first removing it from the tracker of the tile it
     * is in, then from the projectile list, and finally from the projectile map.
     * If it is not found in any of these places, throw an illegal state exception.
     */
    public void removeProjectile(int serialID) {
        GameProjectile gp = lookupProjectile(serialID);
        //indirect projectiles are not tracked my terrain tiles
        if (gp.isDirect() && !(tileAt(gp.getAt().getParentTileCoordinate()).actorList.remove(gp)))
            throw new IllegalStateException("Removed actor not found within its tile tracker.");
        if (!(PROJECTILE_LIST.remove(gp)))
            throw new IllegalStateException("Removed actor not found within the zone's actor list.");
        if (PROJECTILE_MAP.remove(gp) == null)
            throw new IllegalStateException("Removed actor not found within the zone's actor map.");
    }

    public TerrainTile tileAt(TileCoordinate tc) {
        return tileAt(tc.COLUMN, tc.ROW);
    }
    public TerrainTile tileAt(int column, int row) {
        return TERRAIN[row][column];
    }

    private void invariant() {
        if (ACTOR_LIST.size() != ACTOR_MAP.size())
            throw new IllegalStateException("Actor discrepancy - list has size " + ACTOR_LIST.size() +
                    "but map has size " + ACTOR_MAP.size());
        if (PROJECTILE_LIST.size() != PROJECTILE_MAP.size())
            throw new IllegalStateException("Projectile discrepancy - list has size " + PROJECTILE_LIST.size() +
                    "but map has size " + PROJECTILE_MAP.size());
        //todo - more here?
    }

    public void apply(GameZoneUpdate update) {
        try {
            update.METHOD.invoke(this, update.ARGUMENTS);
        } catch (IllegalAccessException e) {
            LogHub.logFatalCrash("Update failure - IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            LogHub.logFatalCrash("Update failure - InvocationTargetException", e);
        }
    }
}
