package com.lonedev.gtroot;

import java.util.List;

/**
 * This is the state of a given game.
 *
 * @author Richard Hawkes
 */
public class GameState {
    private List<Player> players;
    private boolean gameRunning = true;
    private Player currentPlayer;

    public GameState(List<Player> players) {
        this.players = players;
    }

    public void newGame() {
        currentPlayer = players.get(0);
        gameRunning = true;
    }

    public void moveToNextPlayer() {
        System.out.println("Moving to next player...");
        int currentPlayerIndex = players.indexOf(currentPlayer);
        System.out.println("Current player (" + currentPlayerIndex + ") = " + currentPlayer.getPlayerName());
        currentPlayerIndex++;

        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }

        currentPlayer = players.get(currentPlayerIndex);
        System.out.println("Next player (" + currentPlayerIndex + ") = " + currentPlayer.getPlayerName());
    }

    public void playGame() {
        while (gameRunning) {
            // get player to roll dice
            // logic to handle various dice rolls and update the currentPlayer's status.
            //
            // game finished?
            //      yes: winner = XX. gameRunning = false;
            //      no: moveToNextPlayer();
        }
    }
}
