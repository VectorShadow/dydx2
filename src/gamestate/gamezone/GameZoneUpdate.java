package gamestate.gamezone;

import gamestate.TransmittableGameAsset;
import gamestate.gameobject.SerialGameObject;

import java.lang.reflect.Method;

public class GameZoneUpdate extends TransmittableGameAsset {

    public final String METHOD_NAME;

    public final Object[] ARGUMENTS;

    public GameZoneUpdate(String methodName, Object... args) {
        METHOD_NAME = methodName;
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

    public Method toMethod() throws NoSuchMethodException {
        Class<?>[] argClasses = new Class<?>[ARGUMENTS.length];
        for (int i = 0; i < ARGUMENTS.length; ++i)
            argClasses[i] = ARGUMENTS[i].getClass();
        return GameZone.class.getDeclaredMethod(
                METHOD_NAME,
                argClasses
        );
    }
}
