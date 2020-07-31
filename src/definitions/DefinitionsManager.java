package definitions;

import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;

/**
 * Manage all implementation level definitions required to handle actions and interactions beyond the engine context.
 */
public class DefinitionsManager {
    private static GameZoneGenerator gameZoneGenerator = null;
    private static TerrainLookup terrainLookup = null;

    public static void loadDefinitions(GameZoneGenerator gzg, TerrainLookup tl) {
        gameZoneGenerator = gzg;
        terrainLookup = tl;
    }

    public static GameZone generateZone(ZoneCoordinate zc) {
        return gameZoneGenerator.getGameZoneBuilder(zc).build();
    }

    public static TerrainLookup lookupTerrain() {
        return terrainLookup;
    }
}
