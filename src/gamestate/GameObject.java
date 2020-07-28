package gamestate;

import java.io.Serializable;

/**
 * A top level abstraction for all objects tracked by the gamestate.
 */
public abstract class GameObject implements Serializable {
    //all data fields for this GameObject. Primitives are stored as their wrapper classes.
    protected final Object[] fields;

    //when a GameObject is constructed, it must initialize its fields
    public GameObject() {
        fields = declareFields();
    }

    //each subclass must implement this method to create an array of the proper size to support its fields
    protected abstract Object[] declareFields();

    //access a field by index
    public Object get(int fieldIndex) {
        return fields[fieldIndex];
    }

    //chain access sub game objects
    public GameObject GET(int fieldIndex) {
        return (GameObject)fields[fieldIndex];
    }

    //mutate a field by index
    public void set(int fieldIndex, Object object) {
        fields[fieldIndex] = object;
    }
}
