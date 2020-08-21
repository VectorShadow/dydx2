package util;

public enum Direction {
    NORTH(-1, 0),
    NORTH_EAST(-1, 1),
    EAST(0, 1),
    SOUTH_EAST(1, 1),
    SOUTH(1, 0),
    SOUTH_WEST(1, -1),
    WEST(0, -1),
    NORTH_WEST(-1, -1),
    SELF(0, 0);

    public final int ROW_CHANGE;
    public final int COL_CHANGE;

    Direction(int dr, int dc) {
        ROW_CHANGE = dr;
        COL_CHANGE = dc;
    }

    public Direction rotateClockwise() {
        switch (this) {
            case SELF: return SELF;
            case NORTH_WEST: return NORTH;
            case WEST: return NORTH_WEST;
            case SOUTH_WEST: return WEST;
            case SOUTH: return SOUTH_WEST;
            case SOUTH_EAST: return SOUTH;
            case EAST: return SOUTH_EAST;
            case NORTH_EAST: return EAST;
            case NORTH: return NORTH_EAST;
            default: throw new IllegalStateException("Unhandled direction " + this);
        }
    }
    public Direction rotateCounterClockwise() {
        switch (this) {
            case SELF: return SELF;
            case NORTH_WEST: return WEST;
            case WEST: return SOUTH_WEST;
            case SOUTH_WEST: return SOUTH;
            case SOUTH: return SOUTH_EAST;
            case SOUTH_EAST: return EAST;
            case EAST: return NORTH_EAST;
            case NORTH_EAST: return NORTH;
            case NORTH: return NORTH_WEST;
            default: throw new IllegalStateException("Unhandled direction " + this);
        }
    }
    public boolean isDiagonal() {
        return ROW_CHANGE != 0 && COL_CHANGE != 0;
    }

    /**
     * Convert a facing measured in radians to a direction.
     * Note that this is opposite a typical unit circle, since our positive Y coordinates are below the X axis,
     * not above it as in the case of the unit circle.
     */
    public static Direction toDirection(double radians) {
        double adjustedRadians = radians + (Math.PI / 8.0);
        while (adjustedRadians >= Math.PI * 2.0)
            adjustedRadians -= Math.PI * 2.0;
        int directionIndex = (int)Math.floor((adjustedRadians / (Math.PI / 4.0)));
        switch (directionIndex) {
            case 0: return EAST;
            case 1: return SOUTH_EAST;
            case 2: return SOUTH;
            case 3: return SOUTH_WEST;
            case 4: return WEST;
            case 5: return NORTH_WEST;
            case 6: return NORTH;
            case 7: return NORTH_EAST;
            default: throw new IllegalStateException("Unhandled index: " + directionIndex);
        }
    }
}
