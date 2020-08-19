package link.instructions;

public class SelectAvatarInstructionData extends InstructionDatum {
    /**
     * Values >= 0 indicate the index of the avatar within the account metadata to select.
     * Values < 0 indicate creation parameters to generate a new avatar.
     */
    public final int AVATAR_INDEX;

    public final String NAME;

    public SelectAvatarInstructionData(int avatarIndex, String name) {
        AVATAR_INDEX = avatarIndex;
        NAME = name;
    }
}
