package com.lonedev.gtroot.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hawkric
 */
public class RocketRoom extends RocketManagedObject {
    private static final Logger logger = Logger.getLogger(RocketRoom.class.getName());
    /** The set of players in this room. */
    private final Set<ManagedReference<RocketPlayer>> players = new HashSet<ManagedReference<RocketPlayer>>();

    private static final long serialVersionUID = 1L;

    public RocketRoom(String name) {
        super(name);
    }

    public void displayPlayersInRoom() {
        logger.log(Level.INFO, "There are currently {0} player(s) in {1}", new Object[] { players.size(), getName() });
        
        for (ManagedReference<RocketPlayer> player : players) {
            logger.log(Level.INFO, player.get().getName());
        }
    }

    public boolean addPlayer(RocketPlayer player) {
        logger.log(Level.INFO, "{0} enters {1}", new Object[] { player, this });

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return players.add(dataManager.createReference(player));
    }

    public boolean removePlayer(RocketPlayer player) {
        logger.log(Level.INFO, "{0} leaves {1}", new Object[] { player, this });

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        // For the line below to work, I am going to make the assumption that
        // the reference object "created?" is identical to the reference object
        // being removed (ie the hashCode and equals return the same).
        // Otherwise, this remove method just plain wouldn't work. I didn't
        // write this BTW!
        return players.remove(dataManager.createReference(player));
    }
}
