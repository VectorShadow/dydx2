package user;

import main.LogHub;

import java.io.*;
import java.util.ArrayList;

import static user.UserAccountManager.*;

/**
 * This class contains all information about an individual user account.
 */
public class UserAccount implements Serializable {

    public static final int MAX_AVATAR_COUNT = 4; //arbitrary - this may change later

    private final String NAME;
    private final ArrayList<UserAvatar> AVATARS;
    private int currentAvatarIndex = -1;
    //todo - more fields?

    UserAccount(String userName) {
        NAME = userName;
        AVATARS = new ArrayList<>();
    }

    public static UserAccount load(String username) {
        UserAccount ua = null;
        try {
            FileInputStream fis = new FileInputStream(getAccountSaveFileName(username));
            ObjectInputStream ois = new ObjectInputStream(fis);
            ua = (UserAccount) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            LogHub.logFatalCrash("Error loading user account.", e);
        }
        return ua;
    }

    public void save() {
        currentAvatarIndex = -1; //clear the current avatar whenever we save the account
        try {
            FileOutputStream fos = new FileOutputStream(getAccountSaveFileName(NAME), false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (Exception e) {
            LogHub.logFatalCrash("Error saving user account.", e);
        }
    }

    private static String getAccountSaveFileName(String username) throws IOException {
        return getUserDirectoryPath(username).toString() + "/" + ACCOUNT_FILE_NAME;
    }

    /**
     * Add a new UserAvatar to this account.
     * @return the index of the newly added Avatar.
     */
    public int addAvatar(UserAvatar userAvatar) {
        if (AVATARS.size() >= MAX_AVATAR_COUNT)
            throw new IllegalStateException("Attempted to add avatar beyond max capacity.");
        AVATARS.add(userAvatar);
        return AVATARS.size() - 1;
    }

    public AccountMetadata buildMetadata() {
        ArrayList<AvatarMetadata> avatarMetadata = new ArrayList<>();
        for (UserAvatar userAvatar : AVATARS)
            avatarMetadata.add(userAvatar.buildMetadata());
        return new AccountMetadata(avatarMetadata, NAME);
    }

    public UserAvatar getCurrentAvatar() {
        return AVATARS.get(currentAvatarIndex);
    }

    public String getName() {
        return NAME;
    }

    public void setCurrentAvatar(int avatarIndex) {
        currentAvatarIndex = avatarIndex;
    }
}
