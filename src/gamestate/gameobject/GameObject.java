package gamestate.gameobject;

import java.io.Serializable;

/**
 * A top level abstraction for all objects tracked by the gamestate.
 */
public abstract class GameObject implements Serializable {
    public enum GameObjectSubclass {
        ACTOR,
        ITEM,
        PROJECTILE
        //todo - others?
    }

}
