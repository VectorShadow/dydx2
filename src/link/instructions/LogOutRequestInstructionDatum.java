package link.instructions;

public class LogOutRequestInstructionDatum extends InstructionDatum {
    public final String USERNAME;

    public LogOutRequestInstructionDatum(String username) {
        USERNAME = username;
    }
}
