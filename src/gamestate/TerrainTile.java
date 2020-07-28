package gamestate;

import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameItem;
import gamestate.gameobject.GameObject;
import gamestate.gameobject.TerrainFeature;

import java.util.ArrayList;

/**
 * The basic entity which defines the game world.
 */
public class TerrainTile {
    //this value provides implementation specific lookup information
    short ID;

    //define whether there is an interactive terrain feature here
    TerrainFeature terrainFeature = null;

    //list all objects on this tile
    ArrayList<GameItem> itemList = new ArrayList<>();

    //list all actors on this tile
    ArrayList<GameActor> actorList = new ArrayList<>();
}
