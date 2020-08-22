package gamestate.gamezone;

import java.util.Random;

/**
 * GameZoneBuilder is responsible for building uniquely flavored GameZones.
 * Implementations should extend this for each unique flavor of GameZone they wish to build.
 */
public abstract class GameZoneBuilder {
    protected static final Random BUILD_RANDOM = new Random();
    public abstract GameZone build();
}
