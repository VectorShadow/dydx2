package test;

import link.RemoteDataLink;
import main.Client;

import java.awt.*;
import java.io.IOException;

public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        RemoteDataLink rdl = Client.connect(new GameObjectClientTestHandler(), "vps244728.vps.ovh.ca", 29387);
        Thread.sleep(3_000);
        rdl.transmit(
                new GameObjectTestInstruction(
                        new TestGameObject(
                            10,
                            new TestGameSubObject(
                                    new TestNonGameObject(
                                            new Point(3, 5),
                                            "TestName"
                                    )
                            )
                        )
                )
        );
        rdl.transmit(
                new GameObjectTestInstruction(
                        new TestGameObject(
                                -44,
                                new TestGameSubObject(
                                        new TestNonGameObject(
                                                new Point(7, 7),
                                                "TestName2"
                                        )
                                )
                        )
                )
        );
    }
}
