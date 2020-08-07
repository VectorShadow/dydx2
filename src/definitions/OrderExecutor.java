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
        if (UserAccountManager.activeSession == null) return; //nothing to do if no session has begun
        UserAvatar userAvatar = UserAccountManager.activeSession.getCurrentAvatar();
        if (userAvatar == null) return; //nothing to do if a session exists but no avatar has been selected yet
        GameActor userActor = userAvatar.getActor();
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
        GameActor actor = getActor(dataLink);
        if (orderClass.equals(MovementOrder.class))
            actor.setMovementOrder(null);
        else if (orderClass.equals(RotationOrder.class))
            actor.setRotationOrder(null);
        else
            clearImplementationOrder(actor, orderClass);
    }

    protected abstract void clearImplementationOrder(GameActor actor, Class<? extends Order> orderClass);

    /**
     * Set a specified order.
     * Handle Movement and Rotation, and pass others on to implementation specific handling.
     * @param dataLink the link on which the order was transmitted.
     * @param order the order to set.
     */
    public void setOrder(DataLink dataLink, Order order) {
        GameActor actor = getActor(dataLink);
        if (order instanceof MovementOrder)
            actor.setMovementOrder((MovementOrder)order);
        else if (order instanceof RotationOrder)
            actor.setRotationOrder((RotationOrder)order);
        else
            setImplementationOrder(actor, order);
    }

    protected abstract void setImplementationOrder(GameActor actor, Order order);

    /**
     * Get an actor from a specific data link.
     */
    private GameActor getActor(DataLink dataLink) {
        return Engine.getInstance().getUserAccount(dataLink).getCurrentAvatar().getActor();
    }
}
