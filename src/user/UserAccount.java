package user;

import main.LogHub;

import java.io.*;

import static user.UserAccountManager.*;

/**
 * This class contains all information about an individual user account.
 */
public class UserAccount implements Serializable {

    final String NAME;
    //todo - more fields, probably a list of characters

    UserAccount(String userName) {
        NAME = userName;
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
}
