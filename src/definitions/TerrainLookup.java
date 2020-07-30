package definitions;

import gamestate.terrain.TerrainProperties;
import gamestate.terrain.TerrainTile;

/**
 * Provide access to implementation specific terrain details.
 * This class must implement lookup, and would be a good place to define terrain IDs.
 */
public abstract class TerrainLookup {
    protected abstract TerrainProperties lookup(short terrainID);

    public int getEnergyPermission(TerrainTile tt) {
        return lookup(tt.ID).ENERGY_PERMISSION;
    }

    public int getMatterPermission(TerrainTile tt) {
        return lookup(tt.ID).MATTER_PERMISSION;
    }
}
