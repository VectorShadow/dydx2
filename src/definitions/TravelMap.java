package definitions;

import gamestate.coordinates.Coordinate;
import gamestate.coordinates.ZoneCoordinate;
import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;
import gamestate.terrain.TerrainProperties;
import link.DataLink;
import main.Engine;
import user.UserAvatar;
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

    /**
     * Attempt to change the zone of the avatar on the specified link.
     */
    public void changeZone(DataLink dataLink) {
        Engine engine = Engine.getInstance();
        GameZone gameZone = engine.getGameZone(dataLink);
        UserAvatar playerAvatar = engine.getUserAccount(dataLink).getCurrentAvatar();
        GameActor playerActor = playerAvatar.getActor();
        TerrainProperties terrainProperties =
                DefinitionsManager.
                        getTerrainLookup().
                        getProperties(
                                gameZone.tileAt(
                                        playerActor.getAt().getParentTileCoordinate()
                                )
                        );
        if (terrainProperties.TRAVEL_PERMISSION == TerrainProperties.TRAVEL_PERMISSION_NONE)
            return; //cannot travel from here
        ZoneCoordinate originZoneCoordinate = playerAvatar.getAt();
        ZoneCoordinate destinationZoneCoordinate =
                terrainProperties.TRAVEL_PERMISSION == TerrainProperties.TRAVEL_PERMISSION_DOWN
                        ? climbDown(originZoneCoordinate)
                        : terrainProperties.TRAVEL_PERMISSION == TerrainProperties.TRAVEL_PERMISSION_UP
                        ? climbUp(originZoneCoordinate)
                        : travel(terrainProperties.travelDirection(), originZoneCoordinate);
        if (destinationZoneCoordinate == null || destinationZoneCoordinate .equals(originZoneCoordinate))
            throw new IllegalStateException("Failed to determine valid destination.");
        playerAvatar.setAt(destinationZoneCoordinate);
        engine.changeZones(dataLink);
    }
}
