package gamestate.gamezone;

import gamestate.TransmittableGameAsset;
import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.Coordinate;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;
import gamestate.gameobject.MobileGameObject;
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
public class GameZone extends TransmittableGameAsset {

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

    private int checkSum = 0;

    GameZone(int height, int width) {
        ROWS = height;
        COLUMNS = width;
        TERRAIN = new TerrainTile[ROWS][COLUMNS];
        ACTOR_LIST = new ArrayList<>();
        ACTOR_MAP = Collections.synchronizedMap(new HashMap<Integer, GameActor>());
        PROJECTILE_LIST = new ArrayList<>();
        PROJECTILE_MAP = Collections.synchronizedMap(new HashMap<Integer, GameProjectile>());
    }

    public void addActor(GameActor actor) {
        //todo - check this terrain tile to see if it can fit an actor here!
        // does that check go here and throw an exception? or at whatever calls this?
        ACTOR_MAP.put(actor.getSerialID(), actor);
        ACTOR_LIST.add(actor);
        Coordinate c = actor.getAt().getParentTileCoordinate();
        TERRAIN[c.ROW][c.COLUMN].actorList.add(actor);
    }
    public void addProjectile(GameProjectile projectile) {
        PROJECTILE_MAP.put(projectile.getSerialID(), projectile);
        PROJECTILE_LIST.add(projectile);
        Coordinate c = projectile.getAt().getParentTileCoordinate();
        TERRAIN[c.ROW][c.COLUMN].projectileList.add(projectile);
    }

    private GameActor lookupActor(int serialID) {
        return (GameActor) ACTOR_MAP.get(serialID);
    }

    private GameProjectile lookupProjectile(int serialID) {
        return (GameProjectile) PROJECTILE_MAP.get(serialID);
    }

    public int getCheckSum() {
        return checkSum;
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
        moveMobileGameObject(lookupActor(serialID), pc);
    }
    /**
     * Move the specified projectile to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    public void moveProjectile(int serialID, PointCoordinate pc) {
        moveMobileGameObject(lookupProjectile(serialID), pc);
    }
    private void moveMobileGameObject(MobileGameObject mgo, PointCoordinate pc) {
        TerrainTile source = tileAt(mgo.getAt().getParentTileCoordinate());
        TerrainTile destination = tileAt(pc.getParentTileCoordinate());
        boolean isProjectile = mgo instanceof GameProjectile;
        //indirect projectiles are not tracked by terrain tiles.
        if (isProjectile && !((GameProjectile)mgo).isDirect())
            return;
        ArrayList<MobileGameObject> sourceTracker = isProjectile ? source.projectileList : source.actorList;
        ArrayList<MobileGameObject> destinationTracker =
                isProjectile ? destination.projectileList : destination.actorList;
        if (source != destination) {
            if (!sourceTracker.remove(mgo))
                throw new IllegalStateException("Moved actor not found within its source tile tracker.");
            destinationTracker.add(mgo);
        }
        mgo.setAt(pc);
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

    public void rotateActor(int serialID, double facingChange) {
        GameActor ga = lookupActor(serialID);
        rotateMobileGameObject(ga, facingChange);
    }

    public void rotateProjectile(int serialID, double facingChange) {
        GameProjectile gp = lookupProjectile(serialID);
        rotateMobileGameObject(gp, facingChange);
    }

    private void rotateMobileGameObject(MobileGameObject mgo, double facingChange) {
        mgo.rotate(facingChange);
    }



    public TerrainTile tileAt(Coordinate tc) {
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
            ++checkSum;
        } catch (IllegalAccessException e) {
            LogHub.logFatalCrash("Update failure - IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            LogHub.logFatalCrash("Update failure - InvocationTargetException", e);
        }
        System.out.println("Applying update.... actor at: " + ACTOR_LIST.get(0).getAt());
    }
}
