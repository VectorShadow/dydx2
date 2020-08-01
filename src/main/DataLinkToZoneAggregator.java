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

    /**
     * Attempt to load a Zone for each DataLink that isn't yet connected to one.
     */
    void placeZonelessLinks() {
        for (DataLinkSession dls : dataLinkSessions) {
            if (dls.zoneSession == null) {
                DataLink dl = dls.LINK;
                UserAccount ua = dls.userAccount;
                //don't attempt to place a link that has not yet logged into an account or selected an avatar
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
                dls.zoneSession.LINKS.remove(dls);
                LiveLog.log(
                        "Purged expired link session " +
                                (dls.userAccount == null ?
                                        "with no logged in user" :
                                        ("from user " + dls.userAccount.getName())
                                )
                                + ".",
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
}
