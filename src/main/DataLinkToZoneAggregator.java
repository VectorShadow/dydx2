package main;

import definitions.DefinitionsManager;
import gamestate.gameobject.GameActor;
import gamestate.gamezone.GameZone;
import gamestate.coordinates.ZoneCoordinate;
import gamestate.gamezone.GameZoneUpdate;
import link.DataLink;
import link.instructions.GameZoneTransmissionInstructionDatum;
import link.instructions.IdentifyAvatarAndActorInstructionDatum;
import link.instructions.UpdateMetaDataInstructionDatum;
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
     */
    void connectLinkToZone(DataLink dataLink, int userAvatarIndex) {
        DataLinkSession dls = get(dataLink);
        UserAccount userAccount = dls.userAccount;
        //update the frontend's metadata
        dataLink.transmit(new UpdateMetaDataInstructionDatum(userAccount.buildMetadata()));
        userAccount.setCurrentAvatar(userAvatarIndex);
        UserAvatar userAvatar = userAccount.getCurrentAvatar();
        GameZone gz;
        ZoneCoordinate zc = userAvatar.getAt();
        ZoneSession zs = get(zc);
        ZoneKnowledge zk = userAvatar.getZoneKnowledge();
        //pull the game zone from the zone session if one exists, or create one if not
        gz = zs == null ? DefinitionsManager.generateZone(zc) : zs.getGameZone();
        GameActor userActor = userAvatar.getActor();
        if ( //attempt to preserve location and player position
                zk != null && //if the player has any memory
                        (DefinitionsManager.getTravelMap().isStaticLocation(zc) ||  //and either he is at a static location like a town
                                zk.getGameZoneCreationChecksum() == gz.getCreationCheckSum()) //or he is reconnecting to an existing game with a matching creation checksum.
        ) {
            zk.preserveKnowledge(gz); //preserve knowledge
            userAvatar.restoreLastActorLocation(); //and restore position information
        } else {
            zk = new ZoneKnowledge(gz); //otherwise generate a new zone knowledge object, and let the zone place the actor
        }
        userAvatar.setZoneKnowledge(zk);
        //we transmit the game zone before connecting to the processor, so the front end is ready for updates when they begin to arrive
        dataLink.transmit(new GameZoneTransmissionInstructionDatum(gz, zk));
        if (zs == null) addZoneProcessor(dataLink, gz, zc);
        else connect(dataLink, zc);
        GameZoneUpdate addActor = new GameZoneUpdate("addActor", userActor);
        gz.apply(addActor);
        ArrayList<GameZoneUpdate> addActorAsList = new ArrayList<>();
        addActorAsList.add(addActor);
        dataLink.transmit(
                new GameZoneUpdateInstructionDatum(
                        gz.getUpdateCheckSum(),
                        addActorAsList
                )
        );
        //finally, send the frontend a datum specifying which actor serial ID in its gamezone belongs to it
        dataLink.transmit(new IdentifyAvatarAndActorInstructionDatum(userActor.getSerialID(), userAvatarIndex));
        LiveLog.log("Connected user " + userAccount.getName() + " to GameZone at " + zc, INFO);
    }

    /**
     * Disconnect a DataLinkSession from a ZoneSession when a user releases their avatar.
     */
    void disconnectLinkFromZone(DataLink dataLink) {
        DataLinkSession dls = get(dataLink);
        if (dls.zoneSession == null) return; //no need to continue if this link never connected to a zone session
        dls.zoneSession.LINKS.remove(dls);
        dls.userAccount.getCurrentAvatar().saveLastActorLocation();
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
        UserAccount userAccount = get(dataLink).userAccount;
        //since we are transferring zones, not fully disconnecting with the expectation that we might later reconnect
        //to the same zone, we clear the avatar's memory of its actor's last position so that the new game zone can
        //determine proper placement.
        userAccount.getCurrentAvatar().saveLastActorLocation();
        DataLinkSession dls = get(dataLink);
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
            dls.userAccount.getCurrentAvatar().setActor(null); //clear the avatar's actor
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
