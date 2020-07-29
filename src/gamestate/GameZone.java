package gamestate;

import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;

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
    final TerrainTile[][] terrainMap;

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
        terrainMap = new TerrainTile[ROWS][COLUMNS];
        actorList = new ArrayList<>();
        actorMap = Collections.synchronizedMap(new HashMap<Integer, GameActor>());
        projectileList = new ArrayList<>();
        projectileMap = Collections.synchronizedMap(new HashMap<Integer, GameProjectile>());
    }

    public ArrayList<GameActor> listActors() {
        return actorList;
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
