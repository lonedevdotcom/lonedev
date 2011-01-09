package com.lonedev.gtroot.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
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
public class RocketTable extends RocketManagedObject {
    private static final Logger logger = Logger.getLogger(RocketTable.class.getName());
    ManagedReference<RocketPlayer> player1, player2, player3, player4;
    private int tableId;
    private boolean tableAvailable = true;
    private byte currentDiceRoll;
    Random diceRandom = new Random();

    private static final long serialVersionUID = 1L;

    public RocketTable(int tableId) {
        super("Table" + tableId);
        this.tableId = tableId;
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
        logger.log(Level.INFO, "{0} enters {1}", new Object[] { player, this });

        ManagedReference<RocketPlayer> playerRef = AppContext.getDataManager().createReference(player);
        RocketPlayer updatablePlayer = playerRef.getForUpdate();

        AppContext.getDataManager().markForUpdate(this);

        if (player1 == null) {
            player1 = playerRef;
            updatablePlayer.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            updatablePlayer.setPlayerPosition(PlayerPosition.PLAYER1);
            updateTableAvailabilty();
            return true;
        } else if (player == null) {
            player2 = playerRef;
            updatablePlayer.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            updatablePlayer.setPlayerPosition(PlayerPosition.PLAYER2);
            updateTableAvailabilty();
            return true;
        } else if (player3 == null) {
            player3 = playerRef;
            updatablePlayer.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            updatablePlayer.setPlayerPosition(PlayerPosition.PLAYER3);
            updateTableAvailabilty();
            return true;
        } else if (player4 == null) {
            player4 = playerRef;
            updatablePlayer.setMyCurrentTable(AppContext.getDataManager().createReference(this));
            updatablePlayer.setPlayerPosition(PlayerPosition.PLAYER4);
            updateTableAvailabilty();
            return true;
        } else {
            logger.log(Level.WARNING, "Not sure why, but a player has tried to join a table with no free spaces!");
            return false;
        }
    }

    private void updateTableAvailabilty() {
        AppContext.getDataManager().markForUpdate(this);

        if (player1 != null && player2 != null && player3 != null && player4 != null) {
            tableAvailable = false;
        } else {
            tableAvailable = true;
        }
    }

    public boolean removePlayer(RocketPlayer player) {
        logger.log(Level.INFO, "{0} leaves {1}", new Object[] { player, this });

        AppContext.getDataManager().markForUpdate(this);
        
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
}
