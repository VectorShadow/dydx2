package definitions;

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

    /**
     * Attempt to travel to a lower depth within an existing zone location.
     */
    public abstract ZoneCoordinate climbDown(ZoneCoordinate origin);


    /**
     * Attempt to travel to a higher depth within an existing zone location.
     */
    public abstract ZoneCoordinate climbUp(ZoneCoordinate origin);


    /**
     * Return whether the specified ZoneCoordinate corresponds to a pre-defined, static map or not.
     */
    public abstract boolean isStaticLocation(ZoneCoordinate zoneCoordinate);


    /**
     * Attempt to travel to a different location in the specified direction.
     */
    public abstract ZoneCoordinate travel(Direction direction, ZoneCoordinate origin);

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
        playerActor.setTravelFlag(terrainProperties.TRAVEL_PERMISSION); //set the actor's travel flag so the new game zone can place it appropriately
        playerAvatar.setAt(destinationZoneCoordinate); //re-locate the player's avatar to the new zone
        playerAvatar.setZoneKnowledge(null); //clear any memory this avatar has of the current zone
        engine.changeZones(dataLink);
    }
}
