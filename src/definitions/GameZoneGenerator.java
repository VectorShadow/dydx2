package definitions;

import gamestate.coordinates.ZoneCoordinate;
import gamestate.gamezone.GameZoneBuilder;

/**
 * Provide access to implementation specific GameZoneBuilders.
 */
public abstract class GameZoneGenerator {
    public abstract GameZoneBuilder getGameZoneBuilder(ZoneCoordinate zc);
}
