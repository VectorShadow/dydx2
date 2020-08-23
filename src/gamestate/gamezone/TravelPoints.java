package gamestate.gamezone;

import gamestate.TransmittableGameAsset;
import gamestate.coordinates.Coordinate;
import gamestate.terrain.TerrainProperties;
import util.Direction;

public class TravelPoints extends TransmittableGameAsset {
    public final Coordinate[] TRAVEL_TILE_COORDINATES = {null, null, null, null, null, null, null, null, null, null, null};

    /**
     * Set the specified coordinate as the travel tile corresponding to the specified outbound permission.
     * That is, if we place a travel terrain with NORTH travel permission, we want to add its coordinates at the
     * NORTH permission position in the array.
     */
    public void setTravelPointTo(int travelPermissionOutbound, Coordinate travelTileCoordinate) {
        if (travelPermissionOutbound == TerrainProperties.TRAVEL_PERMISSION_NONE)
            throw new IllegalStateException("Cannot set travel point for a tile which has NONE travel permission.");
        else
            TRAVEL_TILE_COORDINATES[travelPermissionOutbound] = travelTileCoordinate;
    }

    /**
     * Return the coordinate of the travel tile with travel permission opposite the inbound travel permission.
     * That is, if we travelled from a tile with SOUTH travel permission, we want to arrive at the tile with NORTH
     * travel permission, and if we travelled from a tile with UP travel permission, we want to arrive at the tile
     * with DOWN travel permission, etc.
     */
    public Coordinate getEntryPointFrom(int travelPermissionInbound) {
        if (travelPermissionInbound < TerrainProperties.TRAVEL_PERMISSION_NONE) {
            for (Direction direction : Direction.values())
                if (direction.ordinal() == travelPermissionInbound)
                    return TRAVEL_TILE_COORDINATES[direction.reverse().ordinal()];
        } else if (travelPermissionInbound == TerrainProperties.TRAVEL_PERMISSION_DOWN)
            return TRAVEL_TILE_COORDINATES[TerrainProperties.TRAVEL_PERMISSION_UP];
        else if (travelPermissionInbound == TerrainProperties.TRAVEL_PERMISSION_UP)
            return TRAVEL_TILE_COORDINATES[TerrainProperties.TRAVEL_PERMISSION_DOWN];
        throw new IllegalStateException("Invalid inbound travelPermission: " + travelPermissionInbound);
    }
}
