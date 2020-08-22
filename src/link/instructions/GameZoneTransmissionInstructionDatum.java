package link.instructions;

import gamestate.gamezone.GameZone;
import user.ZoneKnowledge;

public class GameZoneTransmissionInstructionDatum extends InstructionDatum {
    public final GameZone GAMEZONE;
    public final ZoneKnowledge ZONE_KNOWLEDGE;

    public GameZoneTransmissionInstructionDatum(GameZone gameZone, ZoneKnowledge zoneKnowledge) {
        GAMEZONE = gameZone;
        ZONE_KNOWLEDGE = zoneKnowledge;
    }
}
