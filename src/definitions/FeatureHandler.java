package definitions;

import gamestate.coordinates.Coordinate;
import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;
import gamestate.terrain.TerrainFeature;

/**
 * Handle interactions resulting from actors activating terrain features.
 */
public abstract class FeatureHandler {
    public abstract void interact(
            GameActor activator,
            Coordinate coordinate,
            GameZone gameZone,
            TerrainFeature terrainFeature
    );
}
