package main;

import definitions.DefinitionsManager;
import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import link.DataLink;

import java.util.ArrayList;

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
        dls.zs = zs;
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
        dls.zs.LINKS.remove(dls);
        ZoneSession zs = get(zc);
        dls.zs = zs;
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
    private DataLinkSession get(DataLink dl) {
        for (DataLinkSession dls : dataLinkSessions) {
            if (dls.LINK == dl) return dls;
        }
        throw new IllegalStateException("DataLink not found.");
    }

    /**
     * @return the ZoneSession corresponding to the specified ZoneCoordinate.
     */
    private ZoneSession get(ZoneCoordinate zc) {
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
            if (dls.zs == null) {
                DataLink dl = dls.LINK;
                //do not attempt to place this link in a zone until encryption has been established.
                //todo - we can probably remove this check once we assign accounts to links, since encryption
                // MUST be established before that happens.
                if (!dl.isEncrypted()) continue;
                //todo - links should connect to a player account, and eventually select a character.
                // Once this is set, the character will contain ZoneCoordinates, and these should be used to place
                // the character in either an existing zone or a new one. For now, make a new one:
                ZoneCoordinate zc = new ZoneCoordinate(
                        0,
                        ZoneCoordinate.SURFACE_DEPTH,
                        ZoneCoordinate.GLOBAL_INSTANCE
                );
                ZoneSession zn = get(zc);
                if (zn == null) {
                    addZoneProcessor(dl, DefinitionsManager.generateZone(zc), zc);
                } else {
                    connect(dl, zc);
                }
            }
        }
    }
    void processAll() {
        for (ZoneSession zs : zoneSessions) zs.processTurn();
    }

    /**
     * Remove all ZoneProcessors which are no longer connected to a DataLink.
     */
    void purgeUnconnectedZoneProcessors() {
        zoneSessions.removeIf(zs -> zs.LINKS.size() == 0);
    }
}
