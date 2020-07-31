package link.instructions;

import crypto.Password;

public class AccountCreationRequestInstructionDatum extends InstructionDatum {

    public final String USERNAME;
    public final String SALT;
    public final String HASHED_PASSWORD;

    public AccountCreationRequestInstructionDatum(String username, String password) {
        USERNAME = username;
        SALT = Password.generateRandomSalt();
        HASHED_PASSWORD = Password.hash(Password.salt(SALT, password));
    }
}
