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

    @Override
    public boolean equals(Object o) {
        return o instanceof Coordinate && COLUMN == ((Coordinate)o).COLUMN && ROW == ((Coordinate)o).ROW;
    }

    public boolean isAdjacentTo(Coordinate c) {
        return Math.abs(c.ROW - ROW) < 2 && Math.abs(c.COLUMN - COLUMN) < 2;
    }

    @Override
    public String toString() {
        return "[c(x):" + COLUMN + ",r(y):" + ROW + "]";
    }
}
