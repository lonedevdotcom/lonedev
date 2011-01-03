package com.lonedev.gtroot;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard Hawkes
 */
public class Player {
    private String playerName;
    private List<ModuleType> modules;

    public Player(String playerName) {
        this(playerName, new ArrayList<ModuleType>());
    }

    public Player(String playerName, List<ModuleType> modules) {
        this.playerName = playerName;
        this.modules = modules;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @param playerName the playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @return the modules
     */
    public List<ModuleType> getModules() {
        return modules;
    }

    /**
     * @param modules the modules to set
     */
    public void setModules(List<ModuleType> modules) {
        this.modules = modules;
    }
}
