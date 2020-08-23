package gamestate.gamezone;

import gamestate.coordinates.Coordinate;
import gamestate.terrain.TerrainTile;

import java.util.Random;

/**
 * GameZoneBuilder is responsible for building uniquely flavored GameZones.
 * Implementations should extend this for each unique flavor of GameZone they wish to build.
 */
public abstract class GameZoneBuilder {
    protected static final Random BUILD_RANDOM = new Random();

    protected final int HEIGHT;
    protected final int WIDTH;

    protected final GameZone ZONE;

    public GameZoneBuilder(int size) {
        this(size, size);
    }

    public GameZoneBuilder(int height, int width) {
        HEIGHT = height;
        WIDTH = width;
        ZONE = new GameZone(HEIGHT, WIDTH);
    }

    public abstract GameZone build();

    /**
     * To be used by implementation of build.
     */
    protected final void setTerrainTile(int rows, int columns) {
        TerrainTile terrainTile = generateTile(rows, columns);
        ZONE.TERRAIN[rows][columns] = terrainTile;
        ZONE.incrementCreationChecksum(terrainTile.ID);
    }

    /**
     * Generate a tile appropriate for the specified location in the implementation.
     * This should also set all necessary tile fields, such as features and possibly actors.
     */
    protected abstract TerrainTile generateTile(int rows, int columns);
}
