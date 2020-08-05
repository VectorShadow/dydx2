package gamestate.gameobject;

/**
 * Define an Actor with Agency, capable of generating events within a GameZone.
 * Implementations should extend this, defining fields according to their needs.
 */
public abstract class GameActor extends MobileGameObject {

    private static int serialCount = 1;

    @Override
    public boolean isMaterial() {
        return true; //all actors are made of matter
    }

    @Override
    protected int nextSerialID() {
        return serialCount++;
    }

}
