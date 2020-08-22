package link.instructions;

import user.AccountMetadata;

public class UpdateMetaDataInstructionDatum extends InstructionDatum {

    public final AccountMetadata ACCOUNT_METADATA;

    public UpdateMetaDataInstructionDatum(AccountMetadata accountMetadata) {
        ACCOUNT_METADATA = accountMetadata;
    }
}
