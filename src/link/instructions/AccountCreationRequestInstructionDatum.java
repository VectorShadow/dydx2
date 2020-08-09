package link.instructions;

import crypto.Password;
import main.LiveLog;

public class AccountCreationRequestInstructionDatum extends InstructionDatum {

    public final String USERNAME;
    public final String SALT;
    public final byte[] HASHED_PASSWORD;

    public AccountCreationRequestInstructionDatum(String username, String password) {
        USERNAME = username;
        SALT = Password.generateRandomSalt();
        HASHED_PASSWORD = Password.hash(Password.salt(SALT, password));
        LiveLog.log("AccountCreationRequestInstructionDatum: " +
                "\nUsername: " + username +
                "\nPlaintext Password: " + password +
                "\nRandom salt: " + SALT +
                "\nSalted Password: " + Password.salt(SALT, password) +
                "\nHashed to: " + new String(Password.hash(Password.salt(SALT, password))),
                LiveLog.LogEntryPriority.DEBUG);
    }
}
