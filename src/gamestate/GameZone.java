package gamestate;

import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameProjectile;

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
     * Provide a constant time reference to all actors in this Zone by their unique serial ID.
     * This is achieved by mapping the serial ID values to the actors using the hash function implemented in
     * SerialGameObject.
     * Note that actors are also tracked by each tile individually, and can be referenced by those tile coordinates
     * as well if that is desireable.
     */
    final Map actorMap;

    /**
     * Provide a constant time reference to all projectiles in this Zone by their unique serial ID.
     * This is achieved by mapping the serial ID values to the projectiles using the hash function implemented in
     * SerialGameObject.
     */
    final Map projectileList;

    GameZone(int height, int width) {
        ROWS = height;
        COLUMNS = width;
        terrainMap = new TerrainTile[ROWS][COLUMNS];
        actorMap = Collections.synchronizedMap(new HashMap<Integer, GameActor>());
        projectileList = Collections.synchronizedMap(new HashMap<Integer, GameProjectile>());
    }
}
