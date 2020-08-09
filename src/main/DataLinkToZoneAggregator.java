package main;

import definitions.DefinitionsManager;
import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import gamestate.gamezone.GameZoneUpdate;
import link.DataLink;
import link.RemoteDataLink;
import link.instructions.GameZoneInstructionDatum;
import link.instructions.GameZoneUpdateInstructionDatum;
import user.UserAccount;
import user.UserAvatar;

import java.util.ArrayList;

import static main.LiveLog.LogEntryPriority.*;

/**
 * DataLinkToZoneAggregator provides utilities for organizing and managing DataLinks and ZoneProcessors.
 */
public class DataLinkToZoneAggregator implements DataLinkAggregator{

    private ArrayList<String> connectedUserNames = new ArrayList<>();
    private ArrayList<DataLinkSession> dataLinkSessions = new ArrayList<>();
    private ArrayList<ZoneSession> zoneSessions = new ArrayList<>();

    /**
     * Add a new DataLink to the DataLinkToZoneAggregator.
     * This should only be done when a DataLink is created by a new socket connection.
     */
    public void addDataLink(DataLink dataLink) {
        dataLinkSessions.add(new DataLinkSession(dataLink));
    }

    @Override
    public int countLinks() {
        return dataLinkSessions.size();
    }

    /**
     * Add a new ZoneProcessor to the DataLinkToZoneAggregator.
     * This should only be done after querying the ZoneCoordinate via contains(), finding no existing ZoneProcessor,
     * and requesting a Zone be generated for that coordinate.
     * @param dl the DataLink connecting to the new Zone.
     * @param gz the newly generated GameZone.
     * @param zc the ZoneCoordinate of the GameZone.
     */
    public void addZoneProcessor(DataLink dl, GameZone gz, ZoneCoordinate zc) {
        ZoneSession zs = new ZoneSession(zc, gz);
        DataLinkSession dls = get(dl);
        dls.zoneSession = zs;
        zs.LINKS.add(dls);
        zoneSessions.add(zs);
        LiveLog.log("Connected dataLink to new Zone at " + zc + ". ", INFO);
    }

    /**
     * Connect a DataLink to an existing ZoneProcessor.
     * This should only be done after querying the ZoneCoordinate and finding it exists.
     */
    private void connect(DataLink dl, ZoneCoordinate zc) {
        DataLinkSession dls = get(dl);
        if (dls.zoneSession != null)
            dls.zoneSession.LINKS.remove(dls);
        ZoneSession zs = get(zc);
        dls.zoneSession = zs;
        zs.LINKS.add(dls);
        zs.expired = false; //un-flag this zone as expired if it receives a connection
        LiveLog.log(
                "Connected dataLink to existing Zone at " + zc + ". Zone now has " + zs.LINKS.size() +
                " connected DataLinks.",
                INFO
        );
    }

    /**
     * @return the DataLinkSession corresponding to the specified DataLink.
     */
    DataLinkSession get(DataLink dl) {
        for (DataLinkSession dls : dataLinkSessions) {
            if (dls.LINK == dl) return dls;
        }
        throw new IllegalStateException("DataLink not found.");
    }

    /**
     * @return the ZoneSession corresponding to the specified ZoneCoordinate.
     */
    ZoneSession get(ZoneCoordinate zc) {
        for (ZoneSession zs : zoneSessions) {
            if (zs.COORD.equals(zc)) return zs;
        }
        return null;
    }

    boolean isTrackingUser(UserAccount userAccount) {
        return connectedUserNames.contains(userAccount.getName());
    }

    /**
     * Connect a DataLinkSession to a ZoneSession once a user has selected an avatar.
     */
    void connectLinkToZone(DataLink dataLink, UserAvatar userAvatar) {
        DataLinkSession dls = get(dataLink);
        UserAccount ua = dls.userAccount;
        ua.setCurrentAvatar(userAvatar);
        ZoneCoordinate zc = userAvatar.getAt();
        ZoneSession zs = get(zc);
        GameZone gz;
        if (zs == null) {
            gz = DefinitionsManager.generateZone(zc);
            //transmit the gamezone before connecting, so the client is prepared to receive updates immediately
            dataLink.transmit(new GameZoneInstructionDatum(gz));
            addZoneProcessor(dataLink, gz, zc);
        } else {
            //transmit the gamezone before connecting, so the client is prepared to receive updates immediately
            gz = zs.getGameZone();
            dataLink.transmit(new GameZoneInstructionDatum(gz));
            connect(dataLink, zc);
        }
        GameZoneUpdate addActor = new GameZoneUpdate(
                "addActor",
                userAvatar.constructActor() //engine side actor creation
        );
        gz.apply(addActor);
        ArrayList<GameZoneUpdate> addActorAsList = new ArrayList<>();
        addActorAsList.add(addActor);
        dataLink.transmit(
                new GameZoneUpdateInstructionDatum(
                        gz.getCheckSum(),
                        addActorAsList
                )
        );
        LiveLog.log("Connected user " + ua.getName() + " to GameZone at " + zc, INFO);
    }

    /**
     * Disconnect a DataLinkSession to a ZoneSession when a user releases their avatar.
     */
    void disconnectLinkFromZone(DataLink dataLink) {
        DataLinkSession dls = get(dataLink);
        if (dls.zoneSession == null) return; //no need to continue if this link never connected to a zone session
        dls.zoneSession.LINKS.remove(dls);
        dls.zoneSession.getGameZone().apply( //remove the player's actor from the game zone
                new GameZoneUpdate(
                        "removeActor",
                        dls.userAccount.getCurrentAvatar().getActor().getSerialID()
                )
        ); //no need to transmit this update since the link is being disconnected
        dls.zoneSession = null;
    }

    void processAll() {
        for (ZoneSession zs : zoneSessions) {
            zs.processTurn();
            if (zs.LINKS.isEmpty()) {
                zs.expired = true; //flag this zone as expired for the engine audit
            }
        }
    }

    /**
     * Remove a DataLink whose connection has been lost.
     */
    void purgeExpiredDataLinkSession(DataLink dataLink) {
        DataLinkSession dls = get(dataLink);
        dataLinkSessions.remove(dls);
        if (dls.zoneSession != null) {
            dls.zoneSession.LINKS.remove(dls);
            dls.zoneSession = null;
        }
        StringBuilder logMessageBuilder = new StringBuilder("Purged expired link session ");
        if (dls.userAccount != null) {
            String username = dls.userAccount.getName();
            connectedUserNames.remove(username);
            logMessageBuilder.append("from user \"" + username + "\". ");
            dls.userAccount.save(); //save the user account
            dls.userAccount = null;
        } else {
            logMessageBuilder.append("(no login). ");
        }
        LiveLog.log(
                logMessageBuilder.append( + connectedUserNames.size() + " users remain connected.").toString(),
                INFO
        );
    }

    /**
     * Remove all ZoneProcessors which have been unconnected to a dataLink long enough to expire.
     */
    void purgeUnconnectedZoneSessions() {
        zoneSessions.removeIf(zs -> zs.expired);
    }

    /**
     * Verify that the aggregator conforms to its invariant.
     */
    void testInvariant() {
        int linkConnectionCount = 0;
        int zoneConnectionCount = 0;
        for (DataLinkSession dls : dataLinkSessions)
            linkConnectionCount += (dls.zoneSession == null) ? 0 : 1;
        for (ZoneSession zs : zoneSessions)
            zoneConnectionCount += zs.LINKS.size();
        if (linkConnectionCount != zoneConnectionCount)
            throw new IllegalStateException("Invariant failure - link connections: " + linkConnectionCount +
                    " zone connections: " + zoneConnectionCount);
    }

    /**
     * Start tracking a connected user account (e.g. login)
     */
    void trackUserAccount(DataLink dataLink, UserAccount userAccount) {
        get(dataLink).userAccount = userAccount;
        connectedUserNames.add(userAccount.getName());
        LiveLog.log(
                "User \"" + userAccount.getName() + "\" logged in. " +
                        connectedUserNames.size() + " users are now connected.",
                INFO
        );
    }
}
