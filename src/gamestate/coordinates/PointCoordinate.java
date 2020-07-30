package gamestate.coordinates;

/**
 * Specify the location of an atomic point within a Zone.
 */
public class PointCoordinate extends TileCoordinate {

    /**
     * The number of points per dimension of a tile, such that a tile contains POINTS_PER_TILE * POINTS_PER_TILE unique
     * atomic points.
     */
    public static final int POINTS_PER_TILE = 8;

    private final TileCoordinate PARENT_TILE_COORDINATE;

    public PointCoordinate(TileCoordinate tc, int pointCol, int pointRow) {
        super(
                tc.COLUMN * POINTS_PER_TILE + pointCol,
                tc.ROW * POINTS_PER_TILE + pointRow
        );
        PARENT_TILE_COORDINATE = tc;
    }

    public TileCoordinate getParentTileCoordinate() {
        return PARENT_TILE_COORDINATE;
    }
}
