package test;

import gamestate.gameobject.GameObject;

public class TestGameSubObject extends GameObject {

    public TestGameSubObject(TestNonGameObject tngo) {
        fields[0] = tngo;
        fields[1] = 7.7;
    }
    @Override
    protected Object[] declareFields() {
        return new Object[2];
    }
}
