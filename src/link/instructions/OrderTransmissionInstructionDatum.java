package link.instructions;

import gamestate.order.Order;

public class OrderTransmissionInstructionDatum extends InstructionDatum {
    public final Order ORDER;
    public final Class<? extends Order> ORDER_CLASS;

    /**
     * Set an order.
     * @param order the order to set.
     */
    public OrderTransmissionInstructionDatum(Order order) {
        ORDER = order;
        ORDER_CLASS = null;
    }

    /**
     * Clear an order.
     * @param orderClass the class of the order to clear.
     */
    public OrderTransmissionInstructionDatum(Class<? extends Order> orderClass) {
        ORDER = null;
        ORDER_CLASS = orderClass;
    }
}
