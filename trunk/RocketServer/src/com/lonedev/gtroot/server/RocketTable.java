package com.lonedev.gtroot.server;

import com.lonedev.gtroot.shared.ClientServerMessageInteractor;
import com.lonedev.gtroot.shared.TableStatus;
import com.lonedev.gtroot.shared.Utils;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedReference;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A RocketTable is where players will play their matches. It contains the
 * state of the current game being played (Players, current player etc). Note
 * that players current module count is stored in their respective RocketPlayer
 * reference;
 *
 * @author Richard Hawkes
 */
public class RocketTable extends RocketManagedObject implements ChannelListener {
    private static final Logger logger = Logger.getLogger(RocketTable.class.getName());
    ManagedReference<RocketPlayer> player1Ref, player2Ref, player3Ref, player4Ref, currentPlayerRef;
    private TableStatus currentStatus;
    private boolean tableAvailable = true;
    ManagedReference<Channel> tableChannelRef;
    private GameEngine gameEngine;

    private static final long serialVersionUID = 1L;

    public RocketTable(int tableId) {
        super("Table" + tableId);
        
        Channel tableChannel = AppContext.getChannelManager().createChannel(getName() + "-channel", this, Delivery.RELIABLE);
        tableChannelRef = AppContext.getDataManager().createReference(tableChannel);

        // For now, use the ONLY GameEngine instance (RocketGameEngine). Can
        // make this a bit smarter later...
        // gameEngine = new RocketGameEngine();
        
        setCurrentStatus(TableStatus.EMPTY);
    }

    public boolean isTableAvailable() {
        return tableAvailable;
    }

    public void setTableAvailable(boolean tableAvailable) {
        if (tableAvailable != this.tableAvailable) {
            logger.log(Level.INFO, "Setting tableAvailable to {0} on {1}", new Object[]{tableAvailable, getName()});
            AppContext.getDataManager().markForUpdate(this);
            this.tableAvailable = tableAvailable;
        }
    }


    public boolean addPlayer(RocketPlayer player) {
        ManagedReference<RocketPlayer> playerRef = AppContext.getDataManager().createReference(player);
        AppContext.getDataManager().markForUpdate(this);
        Channel tableChannel = tableChannelRef.getForUpdate(); // ?? Do I really need to get for update when adding new sessions?

        int playerId = 0;

        if (player1Ref == null) {
            player1Ref = playerRef;
            playerId = 1;
        } else if (player2Ref == null) {
            player2Ref = playerRef;
            playerId = 2;
        } else if (player3Ref == null) {
            player3Ref = playerRef;
            playerId = 3;
        } else if (player4Ref == null) {
            player4Ref = playerRef;
            playerId = 4;
        } else {
            // Theoretically, this should never happen as the tableAvailability
            // won't let players in if the table is full.
            logger.log(Level.SEVERE, "Not sure how, but a player has tried to join a table with no free spaces!");
            return false;
        }

        tableChannel.join(player.getClientSessionRef().get());
        player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
        logger.log(Level.INFO, "{0} enters {1} as player " + playerId, new Object[]{player.getName(), this.getName()});
        tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), playerId)));
        updateTableAvailabilty();
        maybeChangeStatusOnAddPlayer();
        return true;
    }

    private void updateTableAvailabilty() {
        if (player1Ref != null && player2Ref != null && player3Ref != null && player4Ref != null) {
            logger.log(Level.INFO, getName() + " is now full!!");
            setTableAvailable(false);
        } else {
            logger.log(Level.INFO, getName() + " still has availability");
            setTableAvailable(true);
        }
    }

    public boolean removePlayer(RocketPlayer player) {
        logger.log(Level.INFO, "{0} leaves {1}", new Object[] { player.getName(), this.getName() });

        AppContext.getDataManager().markForUpdate(this);
        Channel tableChannel = tableChannelRef.getForUpdate();
        tableChannel.leave(player.getClientSessionRef().get());
        
        // The reference created below should be identical to one of the players
        // on our table. I think createReference has some clever logic to
        // not create another reference if one already exists.
        ManagedReference<RocketPlayer> playerRef = AppContext.getDataManager().createReference(player);

        if (playerRef.equals(player1Ref)) {
            player1Ref = null;
        } else if (playerRef.equals(player2Ref)) {
            player2Ref = null;
        } else if (playerRef.equals(player3Ref)) {
            player3Ref = null;
        } else if (playerRef.equals(player4Ref)) {
            player4Ref = null;
        } else {
            logger.log(Level.SEVERE, "player " + player.getName() + " doesn't appear to be one of this table!");
            return false;
        }

        setTableAvailable(true);
        maybeChangeStatusOnRemovePlayer();
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (! (o instanceof RocketTable)) {
            return false;
        } else if (this == o) {
            return true;
        } else {
            return ((RocketTable)o).getName().equals(this.getName());
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }


    private void maybeChangeStatusOnAddPlayer() {
        if (getPlayerCount() == 1) {
            logger.log(Level.INFO, "Just one player, set status to awaiting more players.");
            setCurrentStatus(TableStatus.AWAITING_MORE_PLAYERS);
        } else if (getPlayerCount() == 2) {
            // We have two players, so we must have had only one before... Start
            // the game!
            logger.log(Level.INFO, "Two players. Update status to waiting to play!");
            setCurrentStatus(TableStatus.WAITING_FOR_PLAYER_ROLL);
        }
    }

    private void maybeChangeStatusOnRemovePlayer() {
        if (getPlayerCount() == 0) {
            // No players left set table to empty
            logger.log(Level.INFO, "No players left. Setting status to empty");
            setCurrentStatus(TableStatus.EMPTY);
        } else if (getPlayerCount() == 1) {
            // Down to one player. Oh dear. Set status to awaiting more players
            logger.log(Level.INFO, "Just one player. Setting status to waiting");
            setCurrentStatus(TableStatus.AWAITING_MORE_PLAYERS);
        }
    }

    public void receivedMessage(Channel channel, ClientSession sender, ByteBuffer message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the currentStatus
     */
    public TableStatus getCurrentStatus() {
        return currentStatus;
    }

    public RocketPlayer getCurrentPlayer() {
        return currentPlayerRef.get();
    }

    /**
     * @param currentStatus the currentStatus to set
     */
    public void setCurrentStatus(TableStatus currentStatus) {
        AppContext.getDataManager().markForUpdate(this);
        logger.log(Level.INFO, "Setting status of " + getName() + " to " + currentStatus);
        this.currentStatus = currentStatus;
    }

    public int getPlayerCount() {
        int playerCount = 0;

        if (player1Ref != null) { playerCount++; }
        if (player2Ref != null) { playerCount++; }
        if (player3Ref != null) { playerCount++; }
        if (player4Ref != null) { playerCount++; }

        return playerCount;
    }

    public void processPlayerDiceRollRequest(RocketPlayer player) {
        // PSEUDO-CODE
        //      Check that the current status is AWAITING_PLAYER_ROLL
        //      Check that the player that sent this request matches the current player
        //      Roll the dice
        //      Set status to WAITING_FOR_PLAYER_ROLL_RESPONSE
        //      Send roll information out to channel for all to see (and update)
        throw new UnsupportedOperationException("MUST DO THIS!");
    }

    public void processPlayerResponse(RocketPlayer player, String responseString) {
        // PSEUDO-CODE
        //      Check that we are AWAITING_PLAYER_RESPONSE
        throw new UnsupportedOperationException("MUST DO THIS!");
    }
}
