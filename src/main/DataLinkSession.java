package main;

import link.DataLink;

/**
 * Track a DataLink and the Zone it is connected to.
 */
class DataLinkSession {

    final DataLink LINK;
    ZoneSession zs;

    DataLinkSession(DataLink dataLink) {
        LINK = dataLink;
        zs = null;
    }
}
