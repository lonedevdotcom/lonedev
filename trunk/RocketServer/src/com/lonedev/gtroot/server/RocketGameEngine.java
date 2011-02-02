/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lonedev.gtroot.server;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 *
 * @author Richard
 */
class RocketGameEngine implements GameEngine, Serializable {
    private static final Logger logger = Logger.getLogger(RocketGameEngine.class.getName());
    
    private static final long serialVersionUID = 1L;
    private RocketTable table; // This is the table the game engine is keeping score of.

    public RocketGameEngine(RocketTable table) {
        this.table = table;
    }

    public void processRollRequest(RocketPlayer player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void processRollResponse(RocketPlayer player, String response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getGameStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
