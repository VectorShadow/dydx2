package definitions;

import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;

/**
 * Manage all implementation level definitions required to handle actions and interactions beyond the engine context.
 */
public class DefinitionsManager {
    private static GameZoneGenerator gameZoneGenerator = null;
    private static GameZoneUpdateListener gameZoneUpdateListener = null;
    private static OrderExecutor orderExecutor = null;
    private static TerrainLookup terrainLookup = null;

    public static void loadDefinitions(
            GameZoneGenerator gzg,
            GameZoneUpdateListener gzul,
            OrderExecutor oe,
            TerrainLookup tl
    ) {
        gameZoneGenerator = gzg;
        gameZoneUpdateListener = gzul;
        orderExecutor = oe;
        terrainLookup = tl;
    }

    public static GameZone generateZone(ZoneCoordinate zc) {
        return gameZoneGenerator.getGameZoneBuilder(zc).build();
    }

    public static GameZoneUpdateListener getGameZoneUpdateListener() {
        return gameZoneUpdateListener;
    }

    public static OrderExecutor getOrderExecutor() {
        return orderExecutor;
    }

    public static TerrainLookup getTerrainLookup() {
        return terrainLookup;
    }
}
