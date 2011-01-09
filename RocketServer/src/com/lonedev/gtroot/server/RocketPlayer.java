package com.lonedev.gtroot.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedReference;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Richard Hawkes
 */
public class RocketPlayer extends RocketManagedObject implements ClientSessionListener {
    private static final Logger logger = Logger.getLogger(RocketPlayer.class.getName());
    public static final String MESSAGE_CHARSET = "UTF-8";
    
    private static final long serialVersionUID = 1L;
    private PlayerPosition playerPosition;
    private ManagedReference<ClientSession> clientSession;

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
        String decodedMessage = decodeString(message);
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

    protected static ByteBuffer encodeString(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }

    protected static String decodeString(ByteBuffer buf) {
        try {
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            return new String(bytes, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }

    private void handleClientMessage(String decodedMessage) {
        // The first 5 characters of any message sent by the client
        int messageType = Integer.parseInt(decodedMessage.substring(0, 4));
        String messageBody = decodedMessage.substring(5); // The rest of the message
        switch (messageType) {
            case(ClientMessageType.JOIN_TABLE) :
                // Do what's necessary to join a table.
                break;

        }
    }
}
