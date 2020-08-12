package user;

import gamestate.TransmittableGameAsset;

import java.util.ArrayList;

public class AccountMetadata extends TransmittableGameAsset {
    public final ArrayList<AvatarMetadata> AVATAR_METADATA;
    public final String USERNAME;

    public AccountMetadata(ArrayList<AvatarMetadata> avatarMetadata, String username) {
        AVATAR_METADATA = avatarMetadata;
        USERNAME = username;
    }
}
