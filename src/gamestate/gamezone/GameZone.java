package gamestate.gamezone;

import definitions.DefinitionsManager;
import gamestate.TransmittableGameAsset;
import gamestate.coordinates.PointCoordinate;
import gamestate.coordinates.Coordinate;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;
import gamestate.gameobject.MobileGameObject;
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
public class GameZone extends SerialGameObject {

    private static int serialCount = 1;

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

    /**
     * @Apply() Method:
     * Add the specified actor to this game zone.
     */
    /*
     * When an actor is generated for placement, its coordinates should be set by the placement method. Use that method
     * to validate pre-existing player coordinates(and reset them if necessary).
     */
    //todo - update to reflect avatar constructActor method.
    void addActor(GameActor actor) {
        //todo - check this terrain tile to see if it can fit an actor here!
        // does that check go here and throw an exception? or at whatever calls this?
        ACTOR_MAP.put(actor.getSerialID(), actor);
        ACTOR_LIST.add(actor);
        PointCoordinate pc = actor.getAt();
        if (pc == null) { //todo - MEGAHACK - actor must have a position *before* calling add on it, unless placeActor is implemented completely deterministically(backend vs frontend must have the same outcome)
            placeActor(actor); //todo - this method is currently a hack, fix it
            pc = actor.getAt();
        }
        Coordinate c = pc.getParentTileCoordinate();
        TERRAIN[c.ROW][c.COLUMN].actorList.add(actor);
    }

    /**
     * @Apply() Method:
     * Add the specified projectile to this game zone.
     */
    void addProjectile(GameProjectile projectile) {
        PROJECTILE_MAP.put(projectile.getSerialID(), projectile);
        PROJECTILE_LIST.add(projectile);
        Coordinate c = projectile.getAt().getParentTileCoordinate();
        TERRAIN[c.ROW][c.COLUMN].projectileList.add(projectile);
    }

    public boolean contains(Coordinate coordinate) {
        return
                coordinate.COLUMN >= 0 &&
                        coordinate.COLUMN < COLUMNS &&
                        coordinate.ROW >= 0 &&
                        coordinate.ROW < ROWS;
    }

    public int countColumns() {
        return COLUMNS;
    }

    public int countRows() {
        return ROWS;
    }

    private GameActor lookupActor(Integer serialID) {
        return (GameActor) ACTOR_MAP.get(serialID);
    }

    private GameProjectile lookupProjectile(Integer serialID) {
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
     * @Apply() Method:
     * Move the specified actor to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    void moveActor(Integer serialID, PointCoordinate pc) {
        moveMobileGameObject(lookupActor(serialID), pc);
    }
    /**
     * @Apply() Method:
     * Move the specified projectile to the specified destination point.
     * If it is not found in its source terrain tile's tracker, throw an illegal state exception.
     */
    void moveProjectile(Integer serialID, PointCoordinate pc) {
        moveMobileGameObject(lookupProjectile(serialID), pc);
    }
    private void moveMobileGameObject(MobileGameObject mgo, PointCoordinate pc) {
        Coordinate sourceTileCoordinate = mgo.getAt().getParentTileCoordinate();
        Coordinate destinationTileCoordinate = pc.getParentTileCoordinate();
        TerrainTile source = tileAt(sourceTileCoordinate);
        TerrainTile destination = tileAt(destinationTileCoordinate);
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
        //finally, if we enter a terrain tile that has an automatically triggered feature, handle that interaction
        if (
                mgo instanceof GameActor &&
                        destination.terrainFeature != null &&
                        destination.terrainFeature.isAutoTriggered()
        )
            DefinitionsManager.
                    getFeatureHandler().
                    interact(
                            (GameActor)mgo,
                            destinationTileCoordinate,
                            this,
                            destination.terrainFeature
                    );
    }

    /**
     * Place an actor which does not currently have a point coordinate into this zone.
     * @param gameActor
     */
    private void placeActor(GameActor gameActor){
        //todo - for now, this is a hack. In the future, we should find a good way of doing this - spawn enemies in
        // appropriate regions, player avatar actors at entrances or stairs/ramps, etc.
        Coordinate centerTile = new Coordinate(COLUMNS / 2, ROWS / 2);
        gameActor.setAt(PointCoordinate.centerOf(centerTile));
    }

    /**
     * @Apply() Method:
     * Remove the specified actor from this gamezone, by first removing it from the tracker of the tile it
     * is in, then from the actor list, and finally from the actor map.
     * If it is not found in any of these places, throw an illegal state exception.
     */
    void removeActor(Integer serialID) {
        GameActor ga = lookupActor(serialID);
        if (!(tileAt(ga.getAt().getParentTileCoordinate()).actorList.remove(ga)))
            throw new IllegalStateException("Removed actor not found within its tile tracker.");
        if (!(ACTOR_LIST.remove(ga)))
            throw new IllegalStateException("Removed actor not found within the zone's actor list.");
        if (ACTOR_MAP.remove(serialID) == null)
            throw new IllegalStateException("Removed actor not found within the zone's actor map.");
        ga.setAt(null); //de-locate this actor
    }

    /**
     * @Apply() Method:
     * Remove the specified projectile from this gamezone, by first removing it from the tracker of the tile it
     * is in, then from the projectile list, and finally from the projectile map.
     * If it is not found in any of these places, throw an illegal state exception.
     */
    void removeProjectile(Integer serialID) {
        GameProjectile gp = lookupProjectile(serialID);
        //indirect projectiles are not tracked my terrain tiles
        if (gp.isDirect() && !(tileAt(gp.getAt().getParentTileCoordinate()).actorList.remove(gp)))
            throw new IllegalStateException("Removed actor not found within its tile tracker.");
        if (!(PROJECTILE_LIST.remove(gp)))
            throw new IllegalStateException("Removed actor not found within the zone's actor list.");
        if (PROJECTILE_MAP.remove(gp) == null)
            throw new IllegalStateException("Removed actor not found within the zone's actor map.");
    }

    /**
     * @Apply() Method:
     * Rotate the actor by the specified amount.
     */
    void rotateActor(Integer serialID, Double facingChange) {
        GameActor ga = lookupActor(serialID);
        rotateMobileGameObject(ga, facingChange);
    }


    /**
     * @Apply() Method:
     * Rotate the projectile by the specified amount.
     */
    void rotateProjectile(Integer serialID, Double facingChange) {
        GameProjectile gp = lookupProjectile(serialID);
        rotateMobileGameObject(gp, facingChange);
    }

    private void rotateMobileGameObject(MobileGameObject mgo, Double facingChange) {
        mgo.rotate(facingChange);
    }

    public TerrainTile tileAt(Coordinate tc) {
        return tileAt(tc.COLUMN, tc.ROW);
    }
    private TerrainTile tileAt(int column, int row) {
        return TERRAIN[row][column];
    }

    private void invariant() {
        if (ACTOR_LIST.size() != ACTOR_MAP.size())
            throw new IllegalStateException("Actor discrepancy - list has size " + ACTOR_LIST.size() +
                    "but map has size " + ACTOR_MAP.size());
        if (PROJECTILE_LIST.size() != PROJECTILE_MAP.size())
            throw new IllegalStateException("Projectile discrepancy - list has size " + PROJECTILE_LIST.size() +
                    "but map has size " + PROJECTILE_MAP.size());
        for (GameActor actor : ACTOR_LIST)
            if (TERRAIN[actor.getAt().getParentTileCoordinate().ROW][actor.getAt().getParentTileCoordinate().COLUMN].actorList.isEmpty())
                throw new IllegalStateException("Actor " + actor.getSerialID() + " purports to be at " + actor.getAt().getParentTileCoordinate() + " but terrain actor list there is empty!");
        //todo - more here?
    }

    public void apply(GameZoneUpdate update) {
        try {
            update.invoke(this);
            ++checkSum;
        } catch (IllegalAccessException e) {
            LogHub.logFatalCrash("Update failure - IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            LogHub.logFatalCrash("Update failure - InvocationTargetException", (Exception) e.getCause());
        } catch (NoSuchMethodException e) {
            LogHub.logFatalCrash("Update failure - NoSuchMethodException", e);
        }
        //test
        invariant();
    }

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }

    /**
     * Determine equivalency via serialID.
     * There is a small probability of collision when the server is reset - that is, a newly generated gamezone
     * might have the same serialID as the stored serialID in the loaded avatar's knowledge.
     * This is most likely to occur for the starting zone, which does not cause any problems, but it may occur rarely
     * on other locations, in which case the only consequence will be an existing knowledge of terrain and features
     * that shouldn't exist. In theory this should not occur frequently enough to be disruptive, but if it is,
     * we'll need to find a better solution.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof GameZone && ((GameZone) o).serialID == serialID;
    }
}
