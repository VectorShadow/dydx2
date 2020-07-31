package gamestate.gamezone;

/**
 * GameZoneBuilder is responsible for building uniquely flavored GameZones.
 * Implementations should extend this for each unique flavor of GameZone they wish to build.
 */
public abstract class GameZoneBuilder {
    public abstract GameZone build();
}
