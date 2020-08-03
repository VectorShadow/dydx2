package gamestate.coordinates;

/**
 * Specify the location of an atomic point within a Zone.
 */
public class PointCoordinate extends Coordinate {

    /**
     * The number of points per dimension of a tile, such that a tile contains POINTS_PER_TILE * POINTS_PER_TILE unique
     * atomic points.
     */
    public static final int POINTS_PER_TILE = 8;

    private final Coordinate PARENT_TILE_COORDINATE;

    /**
     * Interpret a coordinate as a point coordinate.
     */
    public PointCoordinate(Coordinate c) {
        this(c.COLUMN, c.ROW);
    }

    /**
     * Construct a point coordinate from absolute zone coordinates.
     */
    public PointCoordinate(int col, int row) {
        this(
                new Coordinate(
                        col / POINTS_PER_TILE,
                        row / POINTS_PER_TILE
                ),
                col % POINTS_PER_TILE,
                row % POINTS_PER_TILE
        );
    }

    /**
     * Construct a point coordinate from a tile coordinate and relative point coordinates.
     */
    public PointCoordinate(Coordinate tc, int pointCol, int pointRow) {
        super(
                tc.COLUMN * POINTS_PER_TILE + pointCol,
                tc.ROW * POINTS_PER_TILE + pointRow
        );
        PARENT_TILE_COORDINATE = tc;
    }

    public Coordinate getParentTileCoordinate() {
        return PARENT_TILE_COORDINATE;
    }

    public static PointCoordinate centerOf(Coordinate coordinate) {
        return new PointCoordinate(coordinate, POINTS_PER_TILE / 2, POINTS_PER_TILE / 2);
    }
}
