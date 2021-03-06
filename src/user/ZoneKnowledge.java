package user;

import gamestate.TransmittableGameAsset;
import gamestate.coordinates.Coordinate;
import gamestate.gamezone.GameZone;
import gamestate.terrain.TerrainFeature;

import java.util.ArrayList;


public class ZoneKnowledge extends TransmittableGameAsset {


    private final int GAME_ZONE_CREATION_CHECKSUM;
    private final int ZONE_COLUMNS;
    private final int ZONE_ROWS;

    private boolean[][] rememberedTiles;
    private boolean[][] revealedFeatures;

    private int memoryChecksum = 0;

    public ZoneKnowledge(GameZone gameZone) {
        GAME_ZONE_CREATION_CHECKSUM = gameZone.getCreationCheckSum();
        ZONE_COLUMNS = gameZone.countColumns();
        ZONE_ROWS = gameZone.countRows();
        rememberedTiles = new boolean[ZONE_ROWS][ZONE_COLUMNS];
        revealedFeatures = new boolean[ZONE_ROWS][ZONE_COLUMNS];
        for (int r = 0; r < ZONE_ROWS; ++r) {
            for (int c = 0; c < ZONE_COLUMNS; ++c){
                rememberedTiles[r][c] = false;
                revealedFeatures[r][c] = false;
            }
        }
    }

    public int getMemoryChecksum() {
        return memoryChecksum;
    }

    public int getGameZoneCreationChecksum() {
        return GAME_ZONE_CREATION_CHECKSUM;
    }

    /**
     * Check whether a coordinate is within the game zone.
     */
    public boolean isInBounds(Coordinate coordinate) {
        return
                coordinate.ROW >= 0 &&
                        coordinate.COLUMN >= 0 &&
                        coordinate.ROW < ZONE_ROWS &&
                        coordinate.COLUMN < ZONE_COLUMNS;
    }

    /**
     * Check whether a coordinate is remembered.
     */
    public boolean isRemembered(Coordinate coordinate) {
        return isInBounds(coordinate) && rememberedTiles[coordinate.ROW][coordinate.COLUMN];
    }

    /**
     * Check whether terrain features at a coordinate have been revealed.
     */
    public boolean isRevealed(Coordinate coordinate, GameZone gameZone) {
        TerrainFeature terrainFeature= gameZone.tileAt(coordinate).terrainFeature;
        return
                isInBounds(coordinate) &&
                        terrainFeature != null &&
                        (!terrainFeature.isHidden() ||
                                revealedFeatures[coordinate.ROW][coordinate.COLUMN]);
    }

    /**
     * Attempt to preserve existing knowledge of a game zone that may have been updated in an avatar's absence.
     * We use this when we re-load an avatar that has been disconnected and reconnects to an existing gamezone.
     * Note that checks should be made wherever this is called to determine whether it is appropriate to do this.
     */
    public ZoneKnowledge preserveKnowledge(GameZone gameZone) {
        ZoneKnowledge zk = new ZoneKnowledge(gameZone);
        zk.rememberedTiles = rememberedTiles;
        zk.revealedFeatures = revealedFeatures;
        return zk;
    }

    /**
     * Add a list of tile coordinates to memory.
     * @param tileCoordinates the list of coordinates to add, usually derived visually.
     * @return the list of tile coordinates which were not previously revealed.
     */
    public ArrayList<Coordinate> rememberTiles(ArrayList<Coordinate> tileCoordinates) {
        return updateMemory(tileCoordinates, false);
    }

    /**
     * Add a list of coordinates of revealed features to memory.
     * @param featureCoordinates the list of coordinates of features to reveal.
     * @return the list of coordinates of terrain features which were not previously known.
     */
    public ArrayList<Coordinate> revealFeatures(ArrayList<Coordinate> featureCoordinates) {
        return updateMemory(featureCoordinates, true);
    }

    /**
     * Helper method for rememberTiles and revealFeatures.
     */
    private ArrayList<Coordinate> updateMemory(ArrayList<Coordinate> coordinates, boolean feature) {
        ArrayList<Coordinate> unknownCoordinates = new ArrayList<>();
        int c, r;
        for (Coordinate coord : coordinates) {
            c = coord.COLUMN;
            r = coord.ROW;
            if (feature ? !revealedFeatures[r][c] : !rememberedTiles[r][c]) {
                if (feature)
                    revealedFeatures[r][c] = true;
                else
                    rememberedTiles[r][c] = true;
                ++memoryChecksum;
                unknownCoordinates.add(coord);
            }
        }
        return unknownCoordinates;
    }
}
