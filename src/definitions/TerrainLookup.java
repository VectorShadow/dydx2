package definitions;

import gamestate.gameobject.MobileGameObject;
import gamestate.terrain.TerrainProperties;
import gamestate.terrain.TerrainTile;

/**
 * Provide access to implementation specific terrain details.
 * This class must implement lookup, and would be a good place to define terrain IDs.
 */
public abstract class TerrainLookup {
    protected abstract TerrainProperties lookup(short terrainID);

    /**
     * Check the access of a MobileGameObject against the permission of a terrain tile's properties.
     * @return whether the object can pass into and through the terrain.
     */
    public boolean checkAccess(MobileGameObject mgo, TerrainTile tt) {
        return mgo.isMaterial() ?
                mgo.getMovementAccess() >= getMatterPermission(tt)
                : (getEnergyPermission(tt) > 0);
    }

    public TerrainProperties getProperties(TerrainTile tt) {
        return lookup(tt.ID);
    }

    private int getEnergyPermission(TerrainTile tt) {
        return lookup(tt.ID).ENERGY_PERMISSION;
    }

    private int getMatterPermission(TerrainTile tt) {
        return lookup(tt.ID).MATTER_PERMISSION;
    }

    private int getTravelPermission(TerrainTile tt) {
        return lookup(tt.ID).TRAVEL_PERMISSION;
    }
}
