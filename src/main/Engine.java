package main;

import static main.LiveLog.LogEntryPriority.*;

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
    private static final long TURN_TIME_SECOND = 1_000;
    private static final long TURN_TIME_ACTION_AVAILABLE = -1;
    private static final long TURN_TIME_EXECUTE_ORDERS = -2;
    private static final long TURN_TIME_PASS_TIME = -3;

    private static int passTime = PASS_TIME_DEFAULT;
    private static long turnTime = TURN_TIME_DEFAULT;
    private static long nextTurnStart = -1;

    /**
     * A list of active connections on data links.
     * In a remote configuration, this contains a number of links corresponding to all active client connections,
     * but is instantiated with no connections.
     * In a local configuration, it will only ever contain a single link to the local frontend.
     */
    private final DataLinkToZoneAggregator LINK_TO_ZONE_AGGREGATOR;

    /**
     * Specify whether this engine is linked remotely or locally.
     */
    private final boolean IS_REMOTE;

    private int turnCount = 0;

    public Engine(DataLinkToZoneAggregator aggregator) {
        LINK_TO_ZONE_AGGREGATOR = aggregator;
        /*
         * A server instantiating an remote engine will do so on startup, before it has accepted any connections.
         * A frontend instantiating a local engine will do so by providing the paired backend local link.
         */
        IS_REMOTE = LINK_TO_ZONE_AGGREGATOR.countLinks() > 0;
    }

    public void run() {
        if (turnTime > 0)
            nextTurnStart = System.currentTimeMillis() + turnTime;
        executionLoop();
    }

    private void executionLoop() {
        for (;;) {
            if (++turnCount % (TURN_TIME_SECOND / turnTime) == 0) audit(); //audit the aggregator once per second
            LINK_TO_ZONE_AGGREGATOR.processAll();
            if (turnTime > 0) {
                nextTurnStart += turnTime;
                long timeUntilNextTurn = nextTurnStart - System.currentTimeMillis();
                if (validateTime(timeUntilNextTurn)) {
                    try {
                        Thread.sleep(timeUntilNextTurn);
                    } catch (InterruptedException e) {
                        LogHub.logFatalCrash("Engine thread interrupted.", e);
                    }
                } else {
                    nextTurnStart = System.currentTimeMillis(); //start the next turn immediately
                }
            } else {
                //todo - handle negative turn time constants appropriately
            }
        }
    }

    /**
     * Return whether the elapsed turn time was less than or equal to the expected turn time.
     */
    private boolean validateTime(long timeUntilNextTurn) {
        if (timeUntilNextTurn < 0) { //log a non-fatal error if the turn execution exceeded its allotted time
            LiveLog.log(
                    "Turn " + turnCount + " execution time exceeded allotted turn time: " +
                            (turnTime - timeUntilNextTurn) + " > " + turnTime,
                    ERROR
            );
            return false;
        }
        if (timeUntilNextTurn < turnTime / 4) { //log a warning if a turn takes the bulk of its allotted time
            LiveLog.log("Turn " + turnCount + " took more than 75% of allotted time.", WARNING);
        } else if (timeUntilNextTurn < turnTime / 2) { //log an alert if the turn takes at least half of its allotted time
            LiveLog.log("Turn " + turnCount + " took more than 50% of allotted time.", ALERT);
        } else if (turnCount % 32 == 0) { //occasionally log acceptable turn execution times as info
            LiveLog.log("Turn took " + (turnTime - timeUntilNextTurn) + "ms.", INFO);
        }
        return true;
    }

    /**
     * Check each active connection to ensure the zone it is connected to has a running ZoneProcessor.
     * Then check each active zone processor to ensure it still has an active connection attached.
     */
    private void audit() {
        //remove any data link sessions which are no longer connected to a client
        LINK_TO_ZONE_AGGREGATOR.purgeExpiredDataLinkSessions();
        //connect new data link sessions which are ready for a zone to the appropriate zone
        LINK_TO_ZONE_AGGREGATOR.placeZonelessLinks();
        //remove any zone sessions which no longer have any data link sessions connected to them
        LINK_TO_ZONE_AGGREGATOR.purgeUnconnectedZoneSessions();
    }
}
