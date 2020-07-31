package main;

import gamestate.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import link.DataLink;

import java.util.ArrayList;

/**
 * ZoneProcessorDataLinkAggregator provides utilities for organizing and managing DataLinks and ZoneProcessors.
 */
public class ZoneProcessorDataLinkAggregator implements DataLinkAggregator{

    private ArrayList<DataLinkNode> dataLinkNodes = new ArrayList<>();
    private ArrayList<ZoneProcessorNode> zoneProcessorNodes = new ArrayList<>();

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

    int countProcessors() {
        return zoneProcessorNodes.size();
    }

    /**
     * Add a new ZoneProcessor to the ZoneProcessorDataLinkAggregator.
     * This should only be done after querying the ZoneCoordinate via contains(), finding no existing ZoneProcessor,
     * and requesting a Zone be generated for that coordinate.
     * @param dataLink the DataLink connecting to the new Zone.
     * @param gameZone the newly generated GameZone.
     * @param zoneCoordinate the ZoneCoordinate of the GameZone.
     */
    public void addZoneProcessor(DataLink dataLink, GameZone gameZone, ZoneCoordinate zoneCoordinate) {
        if (contains(zoneCoordinate))
            throw new IllegalArgumentException("Zone Coordinate " + zoneCoordinate + " already exists.");
        ZoneProcessorNode zpn = new ZoneProcessorNode(zoneCoordinate, gameZone);
        DataLinkNode dln = get(dataLink);
        dln.zpn = zpn;
        zpn.LINKS.add(dln);
        zoneProcessorNodes.add(zpn);
    }

    /**
     * Connect a DataLink to an existing ZoneProcessor.
     * This should only be done after querying the ZoneCoordinate and finding it exists.
     */
    public void connect(DataLink dl, ZoneCoordinate zc) {
        if (!contains(zc))
            throw new IllegalArgumentException("Zone Coordinate " + zc + " did not exist.");
        DataLinkNode dln = get(dl);
        dln.zpn.LINKS.remove(dln);
        ZoneProcessorNode zpn = get(zc);
        dln.zpn = zpn;
        zpn.LINKS.add(dln);
    }

    /**
     * @return whether a ZoneProcessor exists for the specified ZoneCoordinate.
     */
    public boolean contains(ZoneCoordinate zc) {
        for (ZoneProcessorNode zpn : zoneProcessorNodes) {
            if (zpn.COORD.equals(zc)) return true;
        }
        return false;
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
     * @return the ZoneProcessorNode corresponding to the specified ZoneCoordinate.
     */
    private ZoneProcessorNode get(ZoneCoordinate zc) {
        for (ZoneProcessorNode zpn : zoneProcessorNodes) {
            if (zpn.COORD.equals(zc)) return zpn;
        }
        throw new IllegalStateException("ZoneCoordinate " + zc + " not found.");
    }

    public void processAll() {
        for (ZoneProcessorNode zpn : zoneProcessorNodes) zpn.PROCESSOR.processTurn();
    }

    /**
     * Remove all ZoneProcessors which are no longer connected to a DataLink.
     */
    public void purgeUnconnectedZoneProcessors() {
        zoneProcessorNodes.removeIf(zpn -> zpn.LINKS.size() == 0);
    }
}
