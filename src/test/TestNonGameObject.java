package test;

import java.awt.*;
import java.io.Serializable;

public class TestNonGameObject implements Serializable {
    public int x;
    public int y;
    final boolean b;
    public String n;

    public TestNonGameObject(Point p, String name) {
        x = p.x;
        y = p.y;
        b = x == y;
        n = name;
    }

    public boolean isXY() {
        return b;
    }
}
