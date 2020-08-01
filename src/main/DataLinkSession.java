package main;

import link.DataLink;
import user.UserAccount;

/**
 * Track a DataLink and the Zone it is connected to.
 */
public class DataLinkSession {

    final DataLink LINK;
    UserAccount userAccount;
    ZoneSession zoneSession;

    DataLinkSession(DataLink dataLink) {
        LINK = dataLink;
        userAccount = null;
        zoneSession = null;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
