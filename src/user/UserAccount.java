package user;

import main.LogHub;

import java.io.*;
import java.util.ArrayList;

import static user.UserAccountManager.*;

/**
 * This class contains all information about an individual user account.
 */
public class UserAccount implements Serializable {

    /**
     * This is the user account presented to the front end.
     * The FrontEndDataHandler will keep this updated.
     * The implementation will need access to it to track the player's current state.
     */
    public static UserAccount activeSession = null;

    public static final int MAX_AVATAR_COUNT = 4; //arbitrary - this may change later

    private final String NAME;
    private final ArrayList<UserAvatar> AVATARS;
    private UserAvatar currentAvatar = null;
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
        } catch (Exception e) {
            LogHub.logFatalCrash("Error loading user account.", e);
        }
        return ua;
    }

    public void save() {
        currentAvatar = null; //clear the current avatar whenever we save the account
        try {
            FileOutputStream fos = new FileOutputStream(getAccountSaveFileName(NAME));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } catch (Exception e) {
            LogHub.logFatalCrash("Error saving user account.", e);
        }
    }

    private static String getAccountSaveFileName(String username) throws IOException {
        return getUserDirectoryPath(username).toString() + "/" + ACCOUNT_FILE_NAME;
    }

    public UserAvatar getCurrentAvatar() {
        return currentAvatar;
    }

    public String getName() {
        return NAME;
    }

    public void setCurrentAvatar(UserAvatar currentAvatar) {
        this.currentAvatar = currentAvatar;
    }
}
