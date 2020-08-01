package link.instructions;

import user.UserAccount;

public class LogInResponseInstructionDatum extends InstructionDatum {
    public static final int LOGIN_FAILURE_DUPLICATE_ACCOUNT_CREATION = -4;
    public static final int LOGIN_FAILURE_ACCOUNT_ALREADY_CONNECTED = -3;
    public static final int LOGIN_FAILURE_ACCOUNT_DID_NOT_EXIST = -2;
    public static final int LOGIN_FAILURE_INCORRECT_PASSWORD = -1;
    public static final int LOGIN_SUCCESS = 0;

    public final int RESPONSE_CODE;
    public final UserAccount USER_ACCOUNT;

    public LogInResponseInstructionDatum(int code, UserAccount userAccount) {
        RESPONSE_CODE = code;
        USER_ACCOUNT = userAccount;
    }
}
