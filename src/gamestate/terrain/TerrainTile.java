package gamestate.terrain;

import gamestate.TransmittableGameAsset;
import gamestate.gameobject.*;

import java.util.ArrayList;

/**
 * The basic entity which defines the game world.
 */
public class TerrainTile extends TransmittableGameAsset {
    //this value provides implementation specific lookup information
    public short ID;

    //define whether there is an interactive terrain feature here
    public TerrainFeature terrainFeature;

    //list all objects on this tile
    public final ArrayList<GameItem> itemList;

    //list all actors on this tile
    public final ArrayList<MobileGameObject> actorList;

    //list all direct projectiles on this tile
    public final ArrayList<MobileGameObject> projectileList;

    public TerrainTile(int id) {
        ID = (short)id;
        terrainFeature = null;
        itemList = new ArrayList<>();
        actorList = new ArrayList<>();
        projectileList = new ArrayList<>();
    }
}
