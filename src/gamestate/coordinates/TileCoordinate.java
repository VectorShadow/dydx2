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
}
