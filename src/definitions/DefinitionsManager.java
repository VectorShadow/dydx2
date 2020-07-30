package definitions;

/**
 * Manage all implementation level definitions required to handle actions and interactions beyond the engine context.
 */
public class DefinitionsManager {
    private static TerrainLookup terrainLookup;

    public static void loadDefinitions(TerrainLookup tl) {
        terrainLookup = tl;
    }

    public static TerrainLookup lookupTerrain() {
        return terrainLookup;
    }
}
