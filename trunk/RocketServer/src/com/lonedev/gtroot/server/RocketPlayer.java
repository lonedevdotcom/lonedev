package com.lonedev.gtroot.server;

import com.lonedev.gtroot.shared.RocketModule;
import com.lonedev.gtroot.shared.ClientMessageType;
import com.lonedev.gtroot.shared.Utils;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedReference;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Richard Hawkes
 */
public class RocketPlayer extends RocketManagedObject implements ClientSessionListener {
    private static final Logger logger = Logger.getLogger(RocketPlayer.class.getName());
    
    private static final long serialVersionUID = 1L;
    private PlayerPosition playerPosition;
    private ManagedReference<ClientSession> clientSession;
    private List<RocketModule> modules = new ArrayList<RocketModule>();

    private ManagedReference<RocketTable> myCurrentTable;

    public RocketPlayer(ClientSession clientSession) {
        super(clientSession.getName());
        this.clientSession = AppContext.getDataManager().createReference(clientSession);
        logger.log(Level.INFO, "New RocketPlayer instance for " + getName() + " created");
    }

    /**
     * @return the playerPosition
     */
    public PlayerPosition getPlayerPosition() {
        return playerPosition;
    }

    /**
     * @param playerPosition the playerPosition to set
     */
    public void setPlayerPosition(PlayerPosition playerPosition) {
        AppContext.getDataManager().markForUpdate(this);
        this.playerPosition = playerPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (! (o instanceof RocketPlayer)) {
            return false;
        } else if (this == o) {
            return true;
        } else {
            return ((RocketPlayer)o).getName().equals(this.getName());
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * @return the myCurrentTable
     */
    public ManagedReference<RocketTable> getMyCurrentTable() {
        return myCurrentTable;
    }

    /**
     * @param myCurrentTable the myCurrentTable to set
     */
    public void setMyCurrentTable(ManagedReference<RocketTable> myCurrentTable) {
        this.myCurrentTable = myCurrentTable;
    }

    public void receivedMessage(ByteBuffer message) {
        String decodedMessage = Utils.decodeString(message);
        logger.log(Level.INFO, "Received message from " + getName() + ": " + decodedMessage);
        handleClientMessage(decodedMessage);
    }

    public void disconnected(boolean graceful) {
        logger.log(Level.INFO, getName() + " discconected, graceful=" + graceful);

        // Remove the player from the table they're playing on (if they're
        // playing one). Man, I am getting pretty tired of these references
        // bouncing about.
        if (getMyCurrentTable() != null) {
            RocketTable tableRef = getMyCurrentTable().getForUpdate();
            tableRef.removePlayer(this);
        }

        AppContext.getDataManager().removeObject(this);
    }


    private void handleClientMessage(String decodedMessage) {
        int messageType = 0;

        try {
            // The first 5 characters of any message sent by the client
            messageType = Integer.parseInt(decodedMessage.substring(0, 5));
        } catch (NumberFormatException nfe) {
            logger.log(Level.SEVERE, "Received invalid message format from " + getName() + ": " + decodedMessage);
            return;
        }
        
        String messageBody = decodedMessage.substring(5); // The rest of the message
        switch (messageType) {
            case(ClientMessageType.JOIN_TABLE) :
                joinTable();
                break;
            default:
                throw new RuntimeException("Unsupported message type (" + messageType + ")");
        }
    }

    private void joinTable() {
        logger.log(Level.INFO, "JOIN_TABLE request received from " + getName());

        if (this.getMyCurrentTable() != null) {
            logger.log(Level.SEVERE, getName() + " attempting to join a table, but is already on " + this.getMyCurrentTable().get().getName());
            return;
        }

        RocketTable freeTable = ServerUtils.getInstance().getFreeTable();
        
        if (freeTable != null) {
            logger.log(Level.INFO, "Player " + getName() + " joined " + freeTable.getName());
            freeTable.addPlayer(this);
            // The below line is actually already done by the RocketTable instance (in the method above!).
            // this.setMyCurrentTable(AppContext.getDataManager().createReference(freeTable));
        } else {
            logger.log(Level.INFO, "No tables available for " + getName());
        }
    }

    public void addModule(Color colour) {

    }

    public void removeModules(Color colour) {

    }

    public void resetModules() {
        AppContext.getDataManager().markForUpdate(this);
        modules = new ArrayList<RocketModule>();
    }

}
