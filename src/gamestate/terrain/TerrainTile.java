package gamestate.terrain;

import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameItem;
import gamestate.gameobject.GameProjectile;
import gamestate.gameobject.SerialGameObject;

import java.util.ArrayList;

/**
 * The basic entity which defines the game world.
 */
public class TerrainTile {
    //this value provides implementation specific lookup information
    public short ID;

    //define whether there is an interactive terrain feature here
    public TerrainFeature terrainFeature;

    //list all objects on this tile
    public final ArrayList<GameItem> itemList;

    //list all actors on this tile
    public final ArrayList<SerialGameObject> actorList;

    //list all direct projectiles on this tile
    public final ArrayList<SerialGameObject> projectileList;

    public TerrainTile(int id) {
        ID = (short)id;
        terrainFeature = null;
        itemList = new ArrayList<>();
        actorList = new ArrayList<>();
        projectileList = new ArrayList<>();
    }
}
