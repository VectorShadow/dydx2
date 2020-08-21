package link.instructions;

import user.ZoneKnowledge;

/**
 * Send an entire GameZone.
 */
public class ZoneKnowledgeInstructionDatum extends InstructionDatum {
    public final ZoneKnowledge ZONE_KNOWLEDGE;

    public ZoneKnowledgeInstructionDatum(ZoneKnowledge zk) {
        ZONE_KNOWLEDGE = zk;
    }
}
