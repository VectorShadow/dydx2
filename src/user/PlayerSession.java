package user;

import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;

public class PlayerSession {

    //todo - maybe? private String loginPassword or some sort of password token to resend for reoonnection?

    private static int currentActorID = -1;

    private static int currentAvatarIndex = -1;

    private static AccountMetadata accountMetadata = null;

    private static GameZone gameZone = null;

    private static ZoneKnowledge zoneKnowledge = null;

    public static GameActor getActor() {
        return (currentActorID < 0 || gameZone == null)
                ? null
                : (GameActor)gameZone.getActorMap().get(currentActorID);
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

    public static GameZone getGameZone() {
        return gameZone;
    }

    public static ZoneKnowledge getZoneKnowledge() {
        return zoneKnowledge;
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

    public static void setGameZone(GameZone gz) {
        gameZone = gz;
    }
    public static void setZoneKnowledge(ZoneKnowledge zk) {
        zoneKnowledge = zk;
    }
}
