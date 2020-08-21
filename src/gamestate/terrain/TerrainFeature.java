package gamestate.terrain;

import java.io.Serializable;

/**
 * Define a special interactive terrain feature on a tile.
 * Implementations should extend this, defining fields according to their needs.
 */
public interface TerrainFeature extends Serializable {
    boolean isAutoTriggered();
    boolean isHidden();
}
