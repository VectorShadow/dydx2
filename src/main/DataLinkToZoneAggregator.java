package main;

import definitions.DefinitionsManager;
import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import link.DataLink;
import user.UserAccount;

import java.util.ArrayList;
import java.util.Iterator;

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
        dls.zoneSession.LINKS.remove(dls);
        ZoneSession zs = get(zc);
        dls.zoneSession = zs;
        zs.LINKS.add(dls);
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
     * Attempt to load a Zone for each DataLink that isn't yet connected to one.
     */
    void placeZonelessLinks() {
        for (DataLinkSession dls : dataLinkSessions) {
            if (dls.zoneSession == null) {
                DataLink dl = dls.LINK;
                UserAccount ua = dls.userAccount;
                //don't attempt to place a link that has not yet logged into an account or selected an avatar.
                if (ua == null || ua.getCurrentAvatar() == null) continue;
                ZoneCoordinate zc = ua.getCurrentAvatar().getAt();
                ZoneSession zs = get(zc);
                if (zs == null) {
                    addZoneProcessor(dl, DefinitionsManager.generateZone(zc), zc);
                } else {
                    connect(dl, zc);
                }
                LiveLog.log("Connected user " + ua.getName() + " to GameZone at " + zc, INFO);
            }
        }
    }
    void processAll() {
        for (ZoneSession zs : zoneSessions) zs.processTurn();
    }

    /**
     * Remove all DataLinkSessons which have expired DataLinks.
     */
    void purgeExpiredDataLinkSessions() {
        for (Iterator<DataLinkSession> i = dataLinkSessions.iterator(); i.hasNext();) {
            DataLinkSession dls = i.next();
            if (dls.LINK.isExpired()) {
                i.remove();
                if (dls.zoneSession != null)
                    dls.zoneSession.LINKS.remove(dls);
                StringBuilder logMessageBuilder = new StringBuilder("Purged expired link session ");
                if (dls.userAccount != null) {
                    String username = dls.userAccount.getName();
                    logMessageBuilder.append("from user \"" + username + "\". ");
                    connectedUserNames.remove(username);
                } else {
                    logMessageBuilder.append("(no login). ");
                }
                LiveLog.log(
                        logMessageBuilder.append( + connectedUserNames.size() + " users remain connected.").toString(),
                        INFO
                );
            }
        }
    }

    /**
     * Remove all ZoneProcessors which are no longer connected to a DataLink.
     */
    void purgeUnconnectedZoneSessions() {
        zoneSessions.removeIf(zs -> zs.LINKS.size() == 0);
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
    /**
     * Stop tracking a connected user account (e.g. logout, disconnection).
     * This method is provided for engine access, in order to remove a user voluntarily from a (formerly) un-expired
     * Data Link. It will then expire the link so it can be purged during the next audit cycle.
     */
    void unTrackUserAccount(DataLink dataLink, String username) {
        DataLinkSession dls = get(dataLink);
        dls.userAccount = null;
        connectedUserNames.remove(username);
        LiveLog.log(
                "User \"" + username + "\" logged out. " +
                        connectedUserNames.size() + " users remain connected.",
                INFO
        );
        dls.LINK.forceExpiration();
    }
}
