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
     * Store all actors active on this level,
     */
    final Map actorMap;

    /**
     * List all projectiles currently in motion on this map.
     */
    final ArrayList<GameProjectile> projectileList;

    GameZone(int height, int width) {
        ROWS = height;
        COLUMNS = width;
        terrainMap = new TerrainTile[ROWS][COLUMNS];
        actorMap = Collections.synchronizedMap(new HashMap<Integer, GameActor>());
        projectileList = new ArrayList<>();
    }
}
