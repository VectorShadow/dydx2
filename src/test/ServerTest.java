package test;

import main.Server;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        new Server(new GameObjectServerTestHandler(), 29387).start();
    }
}
