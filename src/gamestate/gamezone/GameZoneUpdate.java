package gamestate.gamezone;

import gamestate.TransmittableGameAsset;
import gamestate.gameobject.GameActor;
import gamestate.gameobject.GameItem;
import gamestate.gameobject.GameObject;
import gamestate.gameobject.GameProjectile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GameZoneUpdate extends TransmittableGameAsset {

    public final String METHOD_NAME;

    public final Object[] ARGUMENTS;

    public GameZoneUpdate(String methodName, Object... args) {
        METHOD_NAME = methodName;
        ARGUMENTS = args;
    }

    public void invoke(GameZone gameZone)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] argClasses = new Class<?>[ARGUMENTS.length];
        for (int i = 0; i < ARGUMENTS.length; ++i) {
            Object argument = ARGUMENTS[i];
            argClasses[i] = (argument instanceof GameObject) ?
                    (argument instanceof GameActor) ?
                            GameActor.class
                            : (argument instanceof GameItem) ?
                            GameItem.class
                            : (argument instanceof GameProjectile) ?
                            GameProjectile.class
                            : GameObject.class //todo - add other subclasses if we add them
                    : argument.getClass();
        }
        GameZone.class.getDeclaredMethod(METHOD_NAME, argClasses).invoke(gameZone, ARGUMENTS);
    }
}
