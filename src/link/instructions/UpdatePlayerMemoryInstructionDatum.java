package link.instructions;

import gamestate.coordinates.Coordinate;

import java.util.ArrayList;

public class UpdatePlayerMemoryInstructionDatum extends InstructionDatum {
    public final int CHECKSUM;
    public final boolean FEATURE;
    public final ArrayList<Coordinate> UPDATES;

    public UpdatePlayerMemoryInstructionDatum(int checksum, boolean feature, ArrayList<Coordinate> updates) {
        CHECKSUM = checksum;
        FEATURE = feature;
        UPDATES = updates;
    }

}
