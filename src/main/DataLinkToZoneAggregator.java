package main;

import definitions.DefinitionsManager;
import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import gamestate.gamezone.GameZoneUpdate;
import link.DataLink;
import link.instructions.GameZoneTransmissionInstructionDatum;
import link.instructions.UpdateMetaDataInstructionDatum;
import link.instructions.ZoneKnowledgeInstructionDatum;
import link.instructions.GameZoneUpdateInstructionDatum;
import user.UserAccount;
import user.UserAvatar;
import user.ZoneKnowledge;

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
     * @param gz the GameZone to be processed.
     * @param zc the ZoneCoordinate of the GameZone.
     */
    public void addZoneProcessor(DataLink dl, GameZone gz, ZoneCoordinate zc) {
        ZoneSession zs = new ZoneSession(gz, zc);
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
     * @return the serialID of the actor associated with the connected account and avatar.
     */
    int connectLinkToZone(DataLink dataLink, int userAvatarIndex) {
        DataLinkSession dls = get(dataLink);
        UserAccount ua = dls.userAccount;
        ua.setCurrentAvatar(userAvatarIndex);
        UserAvatar userAvatar = ua.getCurrentAvatar();
        GameZone gz;
        ZoneCoordinate zc = userAvatar.getAt();
        ZoneSession zs = get(zc);
        ZoneKnowledge zk = userAvatar.getZoneKnowledge();
        if (zs == null) { //no zone session is currently processing a zone corresponding to the desired zone coordinates
            gz = DefinitionsManager.generateZone(zc); //generate a new zone for those coordinates
            zk =
                    zk == null //check whether zone knowledge exists
                            ? new ZoneKnowledge(gz) //if not, generate it, otherwise
                            : zk.preserveKnowledge(gz); //attempt to preserve this avatar's zone knowledge if applicable
            userAvatar.setZoneKnowledge(zk); //then update the avatar's zone knowledge accordingly
            //transmit the gamezone before connecting, so the client is prepared to receive updates immediately
            dataLink.transmit(new GameZoneTransmissionInstructionDatum(gz, zk));
            addZoneProcessor(dataLink, gz, zc);
        } else { //a zone session corresponding to the desired coordinates is already being processed
            gz = zs.getGameZone(); //load the existing gamezone
            if (zk != null) //the avatar has existing zone knowledge
                zk = zk.preserveKnowledge(gz); //attempt to preserve this avatar's knowledge of it if applicable
            userAvatar.setZoneKnowledge(zk); //update the avatar's knowledge to the new game zone state
            //transmit the gamezone before connecting, so the client is prepared to receive updates immediately
            dataLink.transmit(new GameZoneTransmissionInstructionDatum(gz, zk));
            connect(dataLink, zc);
        }
        GameActor userActor = userAvatar.getActor();
        GameZoneUpdate addActor = new GameZoneUpdate("addActor", userActor);
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
        return userActor.getSerialID();
    }

    /**
     * Disconnect a DataLinkSession from a ZoneSession when a user releases their avatar.
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

    /**
     * Transfer a link from one ZoneProcessor to another
     */
    void transferLinkToNewZone(DataLink dataLink) {
        disconnectLinkFromZone(dataLink);
        DataLinkSession dls = get(dataLink);
        dataLink.transmit(new UpdateMetaDataInstructionDatum(dls.userAccount.buildMetadata()));
        connectLinkToZone(dataLink, dls.userAccount.getCurrentAvatarIndex());
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
