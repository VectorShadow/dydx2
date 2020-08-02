package gamestate.coordinates;

/**
 * Specify the coordinates of a specific tile within a gamezone.
 */
public class TileCoordinate {
    public final int COLUMN;
    public final int ROW;

    public TileCoordinate(int column, int row) {
        COLUMN = column;
        ROW = row;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TileCoordinate && COLUMN == ((TileCoordinate)o).COLUMN && ROW == ((TileCoordinate)o).ROW;
    }

    @Override
    public String toString() {
        return "[c(x):" + COLUMN + ",r(y):" + ROW + "]";
    }
}
