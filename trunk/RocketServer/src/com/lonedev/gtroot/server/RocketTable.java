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
    private int tableId;
    private TableStatus currentStatus;
    private boolean tableAvailable = true;
    private byte currentDiceRoll;
    Random diceRandom = new Random();
    ManagedReference<Channel> tableChannelRef;

    private static final long serialVersionUID = 1L;

    public RocketTable(int tableId) {
        super("Table" + tableId);
        this.tableId = tableId;
        
        Channel tableChannel = AppContext.getChannelManager().createChannel(getName() + "-channel", this, Delivery.RELIABLE);
        tableChannelRef = AppContext.getDataManager().createReference(tableChannel);

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

    /**
     * @return the currentDiceRoll
     */
    public byte getCurrentDiceRoll() {
        return currentDiceRoll;
    }

    public void rollDice() {
        int nextRoll = diceRandom.nextInt(6);
        setCurrentDiceRoll((byte)nextRoll);
    }

    /**
     * @param currentDiceRoll the currentDiceRoll to set
     */
    public void setCurrentDiceRoll(byte currentDiceRoll) {
        AppContext.getDataManager().markForUpdate(this);
        this.currentDiceRoll = currentDiceRoll;
    }

    public boolean addPlayer(RocketPlayer player) {
        ManagedReference<RocketPlayer> playerRef = AppContext.getDataManager().createReference(player);
        AppContext.getDataManager().markForUpdate(this);
        Channel tableChannel = tableChannelRef.getForUpdate(); // ?? Do I really need to get for update when adding new sessions?

        tableChannel.join(player.getClientSessionRef().get());

        // this logic needs condensing.
        if (player1Ref == null) {
            player1Ref = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            logger.log(Level.INFO, "{0} enters {1} as player 1", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 1)));
            updateTableAvailabilty();
            maybeChangeStatusOnAddPlayer();
            return true;
        } else if (player2Ref == null) {
            player2Ref = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            logger.log(Level.INFO, "{0} enters {1} as player 2", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 2)));
            updateTableAvailabilty();
            maybeChangeStatusOnAddPlayer();
            return true;
        } else if (player3Ref == null) {
            player3Ref = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            logger.log(Level.INFO, "{0} enters {1} as player 3", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 3)));
            updateTableAvailabilty();
            maybeChangeStatusOnAddPlayer();
            return true;
        } else if (player4Ref == null) {
            player4Ref = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            logger.log(Level.INFO, "{0} enters {1} as player 4", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 4)));
            updateTableAvailabilty();
            maybeChangeStatusOnAddPlayer();
            return true;
        } else {
            // Theoretically, this should never happen as the tableAvailability
            // won't let players in if the table is full.
            logger.log(Level.WARNING, "Not sure how, but a player has tried to join a table with no free spaces!");
            tableChannel.leave(player.getClientSessionRef().get());
            return false;
        }
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
            setTableAvailable(true);
            maybeChangeStatusOnRemovePlayer();
            return true;
        } else if (playerRef.equals(player2Ref)) {
            player2Ref = null;
            setTableAvailable(true);
            maybeChangeStatusOnRemovePlayer();
            return true;
        } else if (playerRef.equals(player3Ref)) {
            player3Ref = null;
            setTableAvailable(true);
            maybeChangeStatusOnRemovePlayer();
            return true;
        } else if (playerRef.equals(player4Ref)) {
            player4Ref = null;
            setTableAvailable(true);
            maybeChangeStatusOnRemovePlayer();
            return true;
        } else {
            logger.log(Level.SEVERE, "player " + player.getName() + " doesn't appear to be one of this table!");
            return false;
        }
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
}
