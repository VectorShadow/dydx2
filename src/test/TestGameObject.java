package test;

import gamestate.GameObject;

public class TestGameObject extends GameObject {

    public TestGameObject(int value, TestGameSubObject tgso) {
        super();
        set(0, value);
        set(1, tgso);
    }
    @Override
    protected Object[] declareFields() {
        return new Object[2];
    }
    @Override
    public String toString() {
        return "field 0(int): " + get(0) + "\nfield1(tgso): \n\tfield0(tngo)::is x == y ?: "
                + ((TestNonGameObject)GET(1).get(0)).isXY() + "\n\tfield1(tngo)::(String): "
                + ((TestNonGameObject)GET(1).get(0)).n + "\nfield1(double): " + GET(1).get(1);
    }
}
