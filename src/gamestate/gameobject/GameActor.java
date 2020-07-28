package gamestate.gameobject;

/**
 * Define an Actor with Agency, capable of generating events within a GameZone.
 * Implementations should extend this, defining fields according to their needs.
 */
public abstract class GameActor extends SerialGameObject {

    private static int serialCount = 0;

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }
}
