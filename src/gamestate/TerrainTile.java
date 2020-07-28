package gamestate;

import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameItem;
import gamestate.gameobject.TerrainFeature;

import java.util.ArrayList;

/**
 * The basic entity which defines the game world.
 */
public class TerrainTile {
    //this value provides implementation specific lookup information
    short ID;

    //define whether there is an interactive terrain feature here
    TerrainFeature terrainFeature;

    //list all objects on this tile
    final ArrayList<GameItem> itemList;

    //list all actors on this tile
    final ArrayList<GameActor> actorList;

    public TerrainTile(int id) {
        ID = (short)id;
        terrainFeature = null;
        itemList = new ArrayList<>();
        actorList = new ArrayList<>();
    }
}
