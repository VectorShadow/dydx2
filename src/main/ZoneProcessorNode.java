package main;

import gamestate.GameZone;
import gamestate.coordinates.ZoneCoordinate;

import java.util.ArrayList;

/**
 * Track a ZoneProcessor and all DataLinks currently connected to it.
 */
class ZoneProcessorNode {
    final ZoneCoordinate COORD;
    final ArrayList<DataLinkNode> LINKS;
    final ZoneProcessor PROCESSOR;

    ZoneProcessorNode(ZoneCoordinate zc, GameZone gz) {
        COORD = zc;
        LINKS = new ArrayList<>();
        PROCESSOR = new ZoneProcessor(gz, this);
    }
}
