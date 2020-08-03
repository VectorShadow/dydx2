package link.instructions;

import user.UserAvatar;

public class SelectAvatarInstructionData extends InstructionDatum {
    public final UserAvatar USER_AVATAR;

    public SelectAvatarInstructionData(UserAvatar userAvatar) {
        USER_AVATAR = userAvatar;
    }
}
