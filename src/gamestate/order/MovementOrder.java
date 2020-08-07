package gamestate.order;

public class MovementOrder extends Order {
    public final boolean FORWARD;

    public MovementOrder(boolean forward) {
        FORWARD = forward;
    }

    @Override
    public boolean equals(Order order) {
        return order instanceof MovementOrder && FORWARD == ((MovementOrder) order).FORWARD;
    }
}
