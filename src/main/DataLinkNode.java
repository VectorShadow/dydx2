package main;

import link.DataLink;

/**
 * Track a DataLink and the Zone it is connected to.
 */
class DataLinkNode {

    final DataLink LINK;
    ZoneNode zpn;

    DataLinkNode(DataLink dataLink) {
        LINK = dataLink;
        zpn = null;
    }
}
