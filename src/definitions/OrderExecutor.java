package definitions;

import gamestate.gameobject.GameActor;
import gamestate.order.MovementOrder;
import gamestate.order.Order;
import gamestate.order.RotationOrder;
import link.DataLink;
import main.Engine;
import user.UserAccount;
import user.UserAccountManager;
import user.UserAvatar;

/**
 * Handles order setting and clearing for the engine to implementation specifications.
 */
public abstract class OrderExecutor {

    /**
     * Handle a disconnection for the back end.
     * First we stop any active movement and rotation orders, then we pass the actor on to implementation specific
     * handling.
     * @param dataLink the datalink on which the disconnection occurred.
     */
    public void backEndHandleDisconnection(DataLink dataLink) {
        GameActor userActor = getActor(dataLink);
        if (userActor == null) return;
        clearOrder(dataLink, MovementOrder.class);
        clearOrder(dataLink, RotationOrder.class);
        implementationHandleDisconnection(getActor(dataLink));
    }

    /**
     * Handle a disconnection for the front end.
     * This proceeds exactly as the back end method, since the result must be that back end and front end actor states
     * remain synchronized.
     */
    public void frontEndHandleDisconnection() {
        GameActor userActor = null;
        //todo - the player session  should know which actor is the player's - get this one. if not, nothing to do
        if (userActor == null) return;
        userActor.setMovementOrder(null);
        userActor.setRotationOrder(null);
        implementationHandleDisconnection(userActor);
    }

    protected abstract void implementationHandleDisconnection(GameActor actor);

    /**
     * Clear an order with the specified class.
     * Handle Movement and Rotation, and pass others on to implementation specific handling.
     * @param dataLink the link on which the order was transmitted.
     * @param orderClass the class of the order to clear.
     */
    public void clearOrder(DataLink dataLink, Class<? extends Order> orderClass) {
        GameActor userActor = getActor(dataLink);
        if (userActor == null) return;
        if (orderClass.equals(MovementOrder.class))
            userActor.setMovementOrder(null);
        else if (orderClass.equals(RotationOrder.class))
            userActor.setRotationOrder(null);
        else
            clearImplementationOrder(userActor, orderClass);
    }

    protected abstract void clearImplementationOrder(GameActor actor, Class<? extends Order> orderClass);

    /**
     * Set a specified order.
     * Handle Movement and Rotation, and pass others on to implementation specific handling.
     * @param dataLink the link on which the order was transmitted.
     * @param order the order to set.
     */
    public void setOrder(DataLink dataLink, Order order) {

        GameActor userActor = getActor(dataLink);
        if (userActor == null) return;
        if (order instanceof MovementOrder)
            userActor.setMovementOrder((MovementOrder)order);
        else if (order instanceof RotationOrder)
            userActor.setRotationOrder((RotationOrder)order);
        else
            setImplementationOrder(userActor, order);
    }

    protected abstract void setImplementationOrder(GameActor actor, Order order);

    /**
     * Get an actor from a specific data link.
     * @return the actor associated with the specified data link, or null if any step of the access chain to the actor
     * is null, indicating it is unsafe to proceed.
     */
    private GameActor getActor(DataLink dataLink) {
        UserAccount userAccount = Engine.getInstance().getUserAccount(dataLink);
        if (userAccount == null) return null;
        UserAvatar userAvatar = userAccount.getCurrentAvatar();
        if (userAvatar == null) return null;
        return userAvatar.getActor();
    }
}
