package gamestate.coordinates;

/**
 * Specify the coordinates of a specific tile within a gamezone.
 */
public class Coordinate {
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Coordinate && COLUMN == ((Coordinate)o).COLUMN && ROW == ((Coordinate)o).ROW;
    }

    @Override
    public String toString() {
        return "[c(x):" + COLUMN + ",r(y):" + ROW + "]";
    }
}
