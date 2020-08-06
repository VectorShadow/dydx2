package gamestate.order;

public class RotationOrder extends Order {
    public final boolean CLOCKWISE;

    public RotationOrder(boolean clockwise) {
        CLOCKWISE = clockwise;
    }
}
