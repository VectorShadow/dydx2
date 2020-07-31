package main;

import definitions.DefinitionsManager;
import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import link.DataLink;

import java.util.ArrayList;

import static main.LiveLog.LogEntryPriority.*;

/**
 * ZoneProcessorDataLinkAggregator provides utilities for organizing and managing DataLinks and ZoneProcessors.
 */
public class ZoneProcessorDataLinkAggregator implements DataLinkAggregator{

    private ArrayList<DataLinkNode> dataLinkNodes = new ArrayList<>();
    private ArrayList<ZoneNode> zoneNodes = new ArrayList<>();

    /**
     * Add a new DataLink to the ZoneProcessorDataLinkAggregator.
     * This should only be done when a DataLink is created by a new socket connection.
     */
    public void addDataLink(DataLink dataLink) {
        dataLinkNodes.add(new DataLinkNode(dataLink));
    }

    @Override
    public int countLinks() {
        return dataLinkNodes.size();
    }

    /**
     * Add a new ZoneProcessor to the ZoneProcessorDataLinkAggregator.
     * This should only be done after querying the ZoneCoordinate via contains(), finding no existing ZoneProcessor,
     * and requesting a Zone be generated for that coordinate.
     * @param dl the DataLink connecting to the new Zone.
     * @param gz the newly generated GameZone.
     * @param zc the ZoneCoordinate of the GameZone.
     */
    public void addZoneProcessor(DataLink dl, GameZone gz, ZoneCoordinate zc) {
        ZoneNode zpn = new ZoneNode(zc, gz);
        DataLinkNode dln = get(dl);
        dln.zpn = zpn;
        zpn.LINKS.add(dln);
        zoneNodes.add(zpn);
        LiveLog.log("Connected dataLink to new Zone at " + zc + ". ", INFO);
    }

    /**
     * Connect a DataLink to an existing ZoneProcessor.
     * This should only be done after querying the ZoneCoordinate and finding it exists.
     */
    private void connect(DataLink dl, ZoneCoordinate zc) {
        DataLinkNode dln = get(dl);
        dln.zpn.LINKS.remove(dln);
        ZoneNode zpn = get(zc);
        dln.zpn = zpn;
        zpn.LINKS.add(dln);
        LiveLog.log(
                "Connected dataLink to existing Zone at " + zc + ". Zone now has " + zpn.LINKS.size() +
                " connected DataLinks.",
                INFO
        );
    }

    /**
     * @return the DataLinkNode corresponding to the specified DataLink.
     */
    private DataLinkNode get(DataLink dl) {
        for (DataLinkNode dln : dataLinkNodes) {
            if (dln.LINK == dl) return dln;
        }
        throw new IllegalStateException("DataLink not found.");
    }

    /**
     * @return the ZoneNode corresponding to the specified ZoneCoordinate.
     */
    private ZoneNode get(ZoneCoordinate zc) {
        for (ZoneNode zpn : zoneNodes) {
            if (zpn.COORD.equals(zc)) return zpn;
        }
        return null;
    }

    /**
     * Attempt to load a Zone for each DataLink that isn't yet connected to one.
     */
    void placeZonelessLinks() {
        for (DataLinkNode dln : dataLinkNodes) {
            if (dln.zpn == null) {
                DataLink dl = dln.LINK;
                //todo - links should connect to a player account, and eventually select a character.
                // Once this is set, the character will contain ZoneCoordinates, and these should be used to place
                // the character in either an existing zone or a new one. For now, make a new one:
                ZoneCoordinate zc = new ZoneCoordinate(
                        0,
                        ZoneCoordinate.SURFACE_DEPTH,
                        ZoneCoordinate.GLOBAL_INSTANCE
                );
                ZoneNode zn = get(zc);
                if (zn == null) {
                    addZoneProcessor(dl, DefinitionsManager.generateZone(zc), zc);
                } else {
                    connect(dl, zc);
                }
            }
        }
    }
    void processAll() {
        for (ZoneNode zpn : zoneNodes) zpn.processTurn();
    }

    /**
     * Remove all ZoneProcessors which are no longer connected to a DataLink.
     */
    void purgeUnconnectedZoneProcessors() {
        zoneNodes.removeIf(zpn -> zpn.LINKS.size() == 0);
    }
}
