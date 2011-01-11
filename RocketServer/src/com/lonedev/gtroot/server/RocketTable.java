package com.lonedev.gtroot.server;

import com.lonedev.gtroot.shared.ClientServerMessageInteractor;
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
    ManagedReference<RocketPlayer> player1, player2, player3, player4;
    private int tableId;
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
    }

    public boolean isTableAvailable() {
        return tableAvailable;
    }

    public void setTableAvailable(boolean tableAvailable) {
        logger.log(Level.INFO, "Setting tableAvailable to {0} on {1}", new Object[] { tableAvailable, getName() });
        AppContext.getDataManager().markForUpdate(this);
        this.tableAvailable = tableAvailable;
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
//        RocketPlayer updatablePlayer = playerRef.getForUpdate();

        AppContext.getDataManager().markForUpdate(player);
        AppContext.getDataManager().markForUpdate(this);
        Channel tableChannel = tableChannelRef.getForUpdate();

        tableChannel.join(player.getClientSessionRef().get());

        // this logic needs condensing.
        if (player1 == null) {
            player1 = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            player.setPlayerPosition(PlayerPosition.PLAYER1);
            player.resetModules();
            logger.log(Level.INFO, "{0} enters {1} as player 1", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 1)));
            updateTableAvailabilty();
            return true;
        } else if (player2 == null) {
            player2 = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            player.setPlayerPosition(PlayerPosition.PLAYER2);
            logger.log(Level.INFO, "{0} enters {1} as player 2", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 2)));
            updateTableAvailabilty();
            return true;
        } else if (player3 == null) {
            player3 = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            player.setPlayerPosition(PlayerPosition.PLAYER3);
            logger.log(Level.INFO, "{0} enters {1} as player 3", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 3)));
            updateTableAvailabilty();
            return true;
        } else if (player4 == null) {
            player4 = playerRef;
            player.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            player.setPlayerPosition(PlayerPosition.PLAYER4);
            logger.log(Level.INFO, "{0} enters {1} as player 4", new Object[] { player.getName(), this.getName() });
            tableChannel.send(Utils.encodeString(ClientServerMessageInteractor.createTableJoinSuccessMessage(player.getName(), 4)));
            updateTableAvailabilty();
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
        AppContext.getDataManager().markForUpdate(this);

        if (player1 != null && player2 != null && player3 != null && player4 != null) {
            logger.log(Level.INFO, getName() + " is now full!!");
            tableAvailable = false;
        } else {
            logger.log(Level.INFO, getName() + " still has availability");
            tableAvailable = true;
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

        if (playerRef.equals(player1)) {
            player1.getForUpdate().setPlayerPosition(null);
            player1 = null;
            tableAvailable = true;
            return true;
        } else if (playerRef.equals(player2)) {
            player2.getForUpdate().setPlayerPosition(null);
            player2 = null;
            tableAvailable = true;
            return true;
        } else if (playerRef.equals(player3)) {
            player3.getForUpdate().setPlayerPosition(null);
            player3 = null;
            tableAvailable = true;
            return true;
        } else if (playerRef.equals(player4)) {
            player4.getForUpdate().setPlayerPosition(null);
            player4 = null;
            tableAvailable = true;
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

    public void receivedMessage(Channel channel, ClientSession sender, ByteBuffer message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
