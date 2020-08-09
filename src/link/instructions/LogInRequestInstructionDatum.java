package link.instructions;

import crypto.Password;
import main.LiveLog;

public class LogInRequestInstructionDatum extends InstructionDatum {

    public final String USERNAME;
    private final String PASSWORD;

    public LogInRequestInstructionDatum(String username, String password) {
        USERNAME = username;
        PASSWORD = password;
    }

    /**
     * Access the password for verification against a catalog of users.
     * @param salt the salt value for the user matching this Datum's USERNAME field.
     * @return the salted and hashed password for comparison against the catalog value.
     */
    public byte[] getHashedPassword(String salt) {
        LiveLog.log("Recovering password from LoginRequestInstructionDatum: " +
                "\nUsername: " + USERNAME +
                "\nPlaintext Password: " + PASSWORD +
                "\nRandom salt: " + salt +
                "\nSalted Password: " + Password.salt(salt, PASSWORD) +
                "\nHashed to: " + new String(Password.hash(Password.salt(salt, PASSWORD))),
                LiveLog.LogEntryPriority.DEBUG);
        return Password.hash(Password.salt(salt, PASSWORD));
    }
}
