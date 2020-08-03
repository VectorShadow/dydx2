package gamestate.gameobject;

import gamestate.gamezone.GameZone;

import java.io.Serializable;

/**
 * A top level abstraction for all objects tracked by the gamestate.
 */
public abstract class GameObject implements Serializable {
    //All GameObjects must know which zone they are in.
    private GameZone GAME_ZONE = null;

    public GameZone getGameZone() {
        return GAME_ZONE;
    }

    public void setGameZone(GameZone gameZone) {
        GAME_ZONE = gameZone;
    }
}
