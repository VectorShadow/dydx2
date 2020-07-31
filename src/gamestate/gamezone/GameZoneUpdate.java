package gamestate.gamezone;

import gamestate.gameobject.SerialGameObject;

import java.lang.reflect.Method;

public class GameZoneUpdate {

    public final Method METHOD;

    public final Object[] ARGUMENTS;

    public GameZoneUpdate(Method m, Object... args) {
        METHOD = m;
        ARGUMENTS = new Object[args.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            /*
             * Attempting to pass a SerialGameObject directly will only work on local implementations, or on the
             * back end of a remote implementation. Instead, we pass the object's serialID, so that it can be looked up
             * by the front end.
             */
            ARGUMENTS[i] = arg instanceof SerialGameObject ? ((SerialGameObject)arg).getSerialID() : arg;
        }
    }
}
