package user;

import main.LogHub;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Provides static file management functions related to the user account catalog.
 */
public class UserAccountManager {
    static final String USER_DIRECTORY = "./usr";
    static final Path CATALOG_PATH = Paths.get(USER_DIRECTORY + "/UserCatalog.txt");
    static final String ACCOUNT_FILE_NAME = "userAccount.obj";

    private static final char FIELD_SEPARATOR = '/';

    public static final int USERNAME = 0;
    public static final int SALT = 1;
    public static final int HASHED_PASSWORD = 2;

    /**
     * This is the user account presented to the front end. It must be null on the back end(since the back end tracks
     * multiple sessions and should never refer to a single global session).
     * The FrontEndDataHandler will keep this updated.
     * The implementation will need access to it to track the player's current state.
     */
    public static UserAccount activeSession = null;

    /**
     * Query the user catalog for the specified username.
     * @param username the user name to query.
     * @return a String array containing the username, salt, and hashed password if found, or null if not
     */
    public static String[] queryUsername(String username) {
        BufferedReader bufferedReader = readCatalog();
        String catalogLine;
        String[] parsedCatalogLine;
        try {
            while ((catalogLine = bufferedReader.readLine()) != null) {
                parsedCatalogLine = parseCatalogLine(catalogLine);
                if (parsedCatalogLine[USERNAME].equals(username))
                    return parsedCatalogLine;
            }
        } catch (IOException e) {
            LogHub.logFatalCrash("Error reading user catalog", e);
        }
        return null;
    }

    public static UserAccount createUserAccount(String username, String salt, String hashedPassword) {
        /*
         * Client programs should not make user account creation requests without first making a login
         * attempt and receiving a login response carrying the LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST code.
         * However, nothing absolutely prevents such a request - if it should occur, we simply refuse access
         * by returning null instead of the requested UserAccount.
         */
        if (queryUsername(username) != null)
            return null;
        String catalogLine = username + FIELD_SEPARATOR + salt + FIELD_SEPARATOR + hashedPassword + "\n";
        try {
            Files.write(CATALOG_PATH, catalogLine.getBytes(), StandardOpenOption.APPEND);
            getUserDirectoryPath(username);
        } catch (IOException e) {
            LogHub.logFatalCrash("IOException during user account creation.", e);
        }
        UserAccount userAccount = new UserAccount(username);
        userAccount.save();
        return userAccount;
    }

    public static Path getDirectoryPath() {
        return Paths.get(USER_DIRECTORY);
    }

    static Path getUserDirectoryPath(String username) throws IOException {
        Path userDirectoryPath = Paths.get(USER_DIRECTORY + "/" + username);
        if (!Files.exists(userDirectoryPath))
            Files.createDirectory(userDirectoryPath);
        return userDirectoryPath;
    }

    /**
     * Open the user catalog for reading.
     * @return a BufferedReader for the user catalog.
     */
    private static BufferedReader readCatalog() {
        BufferedReader br = null;
        try {
            if (!Files.exists(Paths.get(USER_DIRECTORY)))
                Files.createDirectory(Paths.get(USER_DIRECTORY));
            if (!Files.exists(CATALOG_PATH))
                Files.createFile(CATALOG_PATH);
            br = Files.newBufferedReader(CATALOG_PATH);
        } catch (IOException e) {
            LogHub.logFatalCrash("Failed to validate user catalog.", e);
        }
        return br;
    }

    private static String[] parseCatalogLine(String catalogLine) {
        int nextSeparator = catalogLine.indexOf(FIELD_SEPARATOR);
        if (nextSeparator < 0)
            throw new IllegalStateException("Improper catalog line - first field separator not found.");
        String[] fields = new String[3];
        fields[0] = catalogLine.substring(0, nextSeparator);
        String remainder = catalogLine.substring(nextSeparator + 1);
        nextSeparator = remainder.indexOf(FIELD_SEPARATOR);
        if (nextSeparator < 0)
            throw new IllegalStateException("Improper catalog line - second field separator not found.");
        fields[1] = remainder.substring(0, nextSeparator);
        fields[2] = remainder.substring(nextSeparator + 1);
        return fields;
    }
}
