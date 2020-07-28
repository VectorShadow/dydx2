package gamestate.gameobject;

/**
 * Define an Actor with Agency, capable of generating events within a GameZone.
 */
public class GameActor extends GameObject {

    private static int spawnCounter = 0;

    final int spawnID;

    public GameActor() {
        spawnID = spawnCounter++;
    }
    @Override
    protected Object[] declareFields() {
        return new Object[1];
    }

    /**
     * Since actors within a Zone are usually generated in quick succession, when the Zone is created,
     * a modulus based hash function provides the fewest collisions. As the number of actors grow beyond the value of
     * GameObject.HASH_CONSTANT, or in situations where actors are added piecemeal after initial spawn, there may be
     * more, but this should provide the best overall performance for general cases.
     */
    @Override
    public int hashCode() {
        return HASH_CONSTANT % spawnID;
    }
}
