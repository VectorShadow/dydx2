package definitions;

import gamestate.coordinates.ZoneCoordinate;
import util.Direction;

/**
 * Facilitate travel between ZoneCoordinates.
 * Implementation must decide how they are linked, what max depths are, what to do when climbing up from depth 0,
 * and how to handle instances.
 */
public abstract class TravelMap {
    public abstract ZoneCoordinate travel(Direction direction, ZoneCoordinate origin);
    public abstract ZoneCoordinate climbDown(ZoneCoordinate origin);
    public abstract ZoneCoordinate climbUp(ZoneCoordinate origin);
}
