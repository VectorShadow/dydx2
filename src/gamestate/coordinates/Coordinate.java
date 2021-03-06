package gamestate.coordinates;

import gamestate.TransmittableGameAsset;
import util.Direction;

import java.io.Serializable;

/**
 * Specify the coordinates of a specific tile within a gamezone.
 */
public class Coordinate extends TransmittableGameAsset {
    public final int COLUMN;
    public final int ROW;

    /**
     * Interpret a pointCoordinate as a simple coordinate.
     */
    public Coordinate(PointCoordinate pointCoordinate) {
        COLUMN = pointCoordinate.COLUMN;
        ROW = pointCoordinate.ROW;
    }

    public Coordinate(int column, int row) {
        COLUMN = column;
        ROW = row;
    }

    /**
     * Find the adjacent coordinate in the specified direction from the source coordinate.
     */
    public Coordinate(Coordinate coordinate, Direction direction) {
        this(coordinate.COLUMN + direction.COL_CHANGE, coordinate.ROW + direction.ROW_CHANGE);
    }

    public double distanceTo(Coordinate c) {
        int dc = c.COLUMN - COLUMN;
        int dr = c.ROW - ROW;
        return Math.sqrt((double)(dc * dc) + (double)(dr * dr));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Coordinate && COLUMN == ((Coordinate)o).COLUMN && ROW == ((Coordinate)o).ROW;
    }

    public boolean isAdjacentTo(Coordinate c) {
        return distanceTo(c) <= Math.sqrt(2.0);
    }

    @Override
    public String toString() {
        return "[c(x):" + COLUMN + ",r(y):" + ROW + "]";
    }
}
