package gamestate.order;

public class RotationOrder extends Order {
    public final boolean CLOCKWISE;

    public RotationOrder(boolean clockwise) {
        CLOCKWISE = clockwise;
    }

    @Override
    public boolean equals(Order order) {
        return order instanceof RotationOrder && CLOCKWISE == ((RotationOrder) order).CLOCKWISE;
    }
}
