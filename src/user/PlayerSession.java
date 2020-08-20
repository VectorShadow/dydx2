package user;

import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;

public class PlayerSession {

    //todo - maybe? private String loginPassword or some sort of password token to resend for reoonnection?

    private static int currentActorID = -1;

    private static int currentAvatarIndex = -1;

    private static AccountMetadata accountMetadata = null;

    public static GameActor getActor() {
        return currentActorID < 0 ? null : (GameActor)GameZone.frontEnd.getActorMap().get(currentActorID);
    }

    public static AccountMetadata getAccountMetadata() {
        return accountMetadata;
    }

    public static int getActorID() {
        return currentActorID;
    }

    public static int getCurrentAvatarIndex() {
        return currentAvatarIndex;
    }

    public static AvatarMetadata getCurrentAvatarMetadata() {
        return accountMetadata.AVATAR_METADATA.get(currentAvatarIndex);
    }

    public static void setAccountMetadata(AccountMetadata metadata) {
        accountMetadata = metadata;
    }

    public static void setActorID(int actorID) {
        currentActorID = actorID;
    }

    public static void setAvatarIndex(int avatarIndex) {
        currentAvatarIndex = avatarIndex;
    }
}
