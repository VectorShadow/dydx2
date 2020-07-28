package main;

import link.DataLink;

import java.util.ArrayList;

/**
 * DisIntegration Engine v2.
 */
public class Engine extends Thread {

    /**
     * Pass time constant. This is the default number of turns that will be executed under the pass time
     * turn time setting, corresponding to one minute of real time if turns take the default time.
     */
    private static final int PASS_TIME_DEFAULT = 240;

    /**
     * Turn time constants.
     *
     * Default is 250ms, or four turns per second.
     *
     * Negative time constants are only available to local instances - a remote instance must support multiple players
     * and cannot modify its behavior for any specific player.
     *
     * Action Available means the engine will run until the player can take any action.
     *
     * Execute Orders means the player has no Event Generators with scheduled orders. This means the engine will run until
     * it completes the last orders issued by the player, then pause for new input. This mode is subject to Class I
     * interrupts.
     *
     * Pass Time means the engine will run until a fixed number of turns have passed. This mode is subject to Class I
     * and class II interrupts.
     */
    private static final long TURN_TIME_DEFAULT = 250;
    private static final long TURN_TIME_ACTION_AVAILABLE = -1;
    private static final long TURN_TIME_EXECUTE_ORDERS = -2;
    private static final long TURN_TIME_PASS_TIME = -3;

    private static int passTime = PASS_TIME_DEFAULT;
    private static long turnTime = TURN_TIME_DEFAULT;
    private static long nextTurnStart = -1;

    /**
     * A list of active connections on data links.
     * In a remote configuration, this points to the same list the Server which instantiated the Engine uses to track
     * its active connections.
     * In a local configuration, the first and only element of the list is the link to the frontend.
     */
    private final ArrayList<DataLink> CONNECTIONS;
    private final ArrayList<ZoneProcessor> ZONE_PROCESSORS;

    /**
     * Specify whether this engine is linked remotely or locally.
     */
    private final boolean IS_REMOTE;

    public Engine(ArrayList<DataLink> connections) {
        CONNECTIONS = connections;
        /*
         * A server instantiating an remote engine will do so on startup, before it has accepted any connections.
         * A frontend instantiating a local engine will do so by providing the paired backend local link.
         */
        IS_REMOTE = CONNECTIONS.isEmpty();
        ZONE_PROCESSORS = new ArrayList<>();
    }

    public void run() {
        if (turnTime > 0)
            nextTurnStart = System.currentTimeMillis() + turnTime;
        executionLoop();
    }

    private void executionLoop() {
        for (;;) {
            audit();
            if (ZONE_PROCESSORS.size() > 0) {
               for (ZoneProcessor zp : ZONE_PROCESSORS)
                   zp.processTurn();
            }
            if (turnTime > 0) {
                nextTurnStart += turnTime;
                long timeUntilNextTurn = nextTurnStart - System.currentTimeMillis();
                if (timeUntilNextTurn < 0) //todo - handle this by dynamically adjusting turn times?
                    throw new IllegalStateException("Turn execution time exceeded allotted turn time: " +
                            (turnTime - timeUntilNextTurn) + " > " + turnTime);
                try {
                    Thread.sleep(timeUntilNextTurn);
                } catch (InterruptedException e) {
                    LogHub.logFatalCrash("Engine thread interrupted.", e);
                }
            } else {
                //todo - handle negative turn time constants appropriately
            }
        }
    }

    /**
     * Check each active connection to ensure the zone it is connected to has a running ZoneProcessor.
     * Then check each active zone processor to ensure it still has an active connection attached.
     */
    private void audit() {
        //todo
    }

    private void addZoneProcessor(ZoneProcessor zp) {
        ZONE_PROCESSORS.add(zp);
    }
    private void removeZoneProcessor(ZoneProcessor zp) {
        ZONE_PROCESSORS.remove(zp);
    }
}
