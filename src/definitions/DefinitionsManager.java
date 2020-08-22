package definitions;

import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;

/**
 * Manage all implementation level definitions required to handle actions and interactions beyond the engine context.
 */
public class DefinitionsManager {
    private static AvatarManager avatarManager = null;
    private static FeatureHandler featureHandler = null;
    private static GameZoneGenerator gameZoneGenerator = null;
    private static GameZoneUpdateListener gameZoneUpdateListener = null;
    private static LoginResponseHandler loginResponseHandler = null;
    private static OrderExecutor orderExecutor = null;
    private static TerrainLookup terrainLookup = null;
    private static TravelMap travelMap = null;

    public static void loadDefinitions(
            AvatarManager am,
            FeatureHandler fh,
            GameZoneGenerator gzg,
            GameZoneUpdateListener gzul,
            LoginResponseHandler lrh,
            OrderExecutor oe,
            TerrainLookup tl,
            TravelMap tm
    ) {
        avatarManager = am;
        featureHandler = fh;
        gameZoneGenerator = gzg;
        gameZoneUpdateListener = gzul;
        loginResponseHandler = lrh;
        orderExecutor = oe;
        terrainLookup = tl;
        travelMap = tm;
    }

    public static GameZone generateZone(ZoneCoordinate zc) {
        return gameZoneGenerator.getGameZoneBuilder(zc).build();
    }

    public static AvatarManager getAvatarManager() {
        return avatarManager;
    }

    public static FeatureHandler getFeatureHandler() {
        return featureHandler;
    }

    public static GameZoneUpdateListener getGameZoneUpdateListener() {
        return gameZoneUpdateListener;
    }

    public static LoginResponseHandler getLoginResponseHandler() {
        return loginResponseHandler;
    }

    public static OrderExecutor getOrderExecutor() {
        return orderExecutor;
    }

    public static TerrainLookup getTerrainLookup() {
        return terrainLookup;
    }

    public static TravelMap getTravelMap() {
        return travelMap;
    }
}
