package gamestate.order;

public class MovementOrder extends Order {
    public final boolean FORWARD;

    public MovementOrder(boolean forward) {
        FORWARD = forward;
    }
}
