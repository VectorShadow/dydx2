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
    int rows;
    int columns;

    /**
     * Define the terrain within the GameZone.
     */
    TerrainTile[][] terrainMap;

    /**
     * Store all actors active on this level,
     */
    Map actorMap = Collections.synchronizedMap(new HashMap<Integer, GameActor>());

    /**
     * List all projectiles currently in motion on this map.
     */
    ArrayList<GameProjectile> projectileList;
}
