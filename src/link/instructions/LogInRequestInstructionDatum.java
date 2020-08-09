package link.instructions;

import crypto.Password;

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
    public String getHashedPassword(String salt) {
        return Password.hash(Password.salt(salt, PASSWORD));
    }
}
