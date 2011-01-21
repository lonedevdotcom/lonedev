package com.lonedev.gtroot.server;

import com.lonedev.gtroot.shared.ClientServerMessageInteractor;
import com.lonedev.gtroot.shared.TableStatus;
import com.lonedev.gtroot.shared.Utils;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedReference;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Richard Hawkes
 */
public class RocketPlayer extends RocketManagedObject implements ClientSessionListener {
    private static final Logger logger = Logger.getLogger(RocketPlayer.class.getName());
    
    private static final long serialVersionUID = 1L;

    private ManagedReference<ClientSession> clientSessionRef;

    private ManagedReference<RocketTable> myCurrentTableRef;

    public RocketPlayer(ClientSession clientSession) {
        super(clientSession.getName());
        this.clientSessionRef = AppContext.getDataManager().createReference(clientSession);
        ServerUtils.getInstance().addServerChatChannelSession(clientSession);
        logger.log(Level.INFO, "New RocketPlayer instance for " + getName() + " created");
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
    public ManagedReference<RocketTable> getMyCurrentTableRef() {
        return myCurrentTableRef;
    }

    /**
     * @param myCurrentTable the myCurrentTable to set
     */
    public void setMyCurrentTable(ManagedReference<RocketTable> myCurrentTableRef) {
        this.myCurrentTableRef = myCurrentTableRef;
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
        if (getMyCurrentTableRef() != null) {
            RocketTable currentTable = getMyCurrentTableRef().get();
            currentTable.removePlayer(this);
        }

        // Also remove them from the server chat channel
        // NOT REQUIRED - THE SESSION IS AUTOMATICALLY REMOVED FROM ALL
        // CHANNELS ON DISCONNECT. RUNNING THIS WILL THROW AN EXCEPTION.
//        ServerUtils.getInstance().removeServerChatChannelSession(clientSessionRef.get());

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
            case ClientServerMessageInteractor.JOIN_TABLE :
                joinTable();
                break;
            case ClientServerMessageInteractor.TABLE_STATUS_REQUEST :
                sendCurrentTableStatusToClientSession();
                break;
            case ClientServerMessageInteractor.ROLL_DICE_REQUEST :
                processRollDiceRequest();
                break;
            case ClientServerMessageInteractor.PLAYER_ROLL_RESPONSE :
                processRollResponse(messageBody);
                break;
            default:
                throw new RuntimeException("Unsupported message type (" + messageType + ")");
        }
    }

    private void joinTable() {
        logger.log(Level.INFO, "JOIN_TABLE request received from " + getName());

        if (this.getMyCurrentTableRef() != null) {
            logger.log(Level.SEVERE, getName() + " attempting to join a table, but is already on " + this.getMyCurrentTableRef().get().getName());
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

    /**
     * @return the clientSessionRef
     */
    public ManagedReference<ClientSession> getClientSessionRef() {
        return clientSessionRef;
    }

    /**
     * @param clientSessionRef the clientSessionRef to set
     */
    public void setClientSessionRef(ManagedReference<ClientSession> clientSessionRef) {
        this.clientSessionRef = clientSessionRef;
    }

    private void sendCurrentTableStatusToClientSession() {
        if (myCurrentTableRef != null) {
            TableStatus status = myCurrentTableRef.get().getCurrentStatus();
            RocketPlayer currentPlayer = myCurrentTableRef.get().getCurrentPlayer();
            clientSessionRef.get().send(Utils.encodeString("TABLE_STATUS:" + status.toString() + ":" + currentPlayer));
        } else {
            logger.log(Level.SEVERE, "Player " + getName() + " attempting to get a table status, but not assigned one!");
            clientSessionRef.get().send(Utils.encodeString("TABLE_STATUS:UNASSIGNED:null"));
        }
    }

    /**
     * This method sends the request to the table to roll the dice. The results
     * are published back on the table channel for all to see, so nothing much
     * to do here.
     */
    private void processRollDiceRequest() {
        if (myCurrentTableRef != null) {
            myCurrentTableRef.get().processPlayerDiceRollRequest(this);
        } else {
            logger.log(Level.SEVERE, "Player " + getName() + " is attempting to roll the dice, but is not assigned a table!");
        }
    }

    private void processRollResponse(String messageBody) {
        if (myCurrentTableRef != null) {
            myCurrentTableRef.get().processPlayerResponse(this, messageBody);
        } else {
            logger.log(Level.SEVERE, "Player " + getName() + " is attempting to send responses to a roll, but is not assigned a table!");
        }
    }

}
