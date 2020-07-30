package gamestate.gameobject;

import gamestate.coordinates.PointCoordinate;

import java.io.Serializable;

/**
 * A top level abstraction for all objects tracked by the gamestate.
 */
abstract class GameObject implements Serializable {
    protected PointCoordinate at;

}
