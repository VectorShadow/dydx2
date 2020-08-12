package link.instructions;

import user.AccountMetadata;

/**
 * Transmit the SerialID belonging to the identified actor.
 * This is how we inform the frontend which actor in its gamezone belongs to it.
 */
public class IdentifyAvatarAndActorInstructionDatum extends InstructionDatum {
    public final AccountMetadata ACCOUNT_METADATA;
    public final int ACTOR_ID;
    public final int AVATAR_INDEX;

    public IdentifyAvatarAndActorInstructionDatum(AccountMetadata accountMetadata, int actorID, int avatarIndex) {
        ACCOUNT_METADATA = accountMetadata;
        ACTOR_ID = actorID;
        AVATAR_INDEX = avatarIndex;
    }
}
