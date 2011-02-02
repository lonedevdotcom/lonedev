package com.lonedev.gtroot.server;

/**
 * Implementations of this interface handle a table current game status (ie
 * scores). The table will pass requests to it, and it should update the
 * game status as appropriate. A call to getGameStatus() will return a string
 * that is passed straight back on the tables channel.
 *
 * I am pretty sure any implementation must implement Serializable too.
 *
 * @author Richard Hawkes
 */
public interface GameEngine {
    /**
     * Handles the roll request from a given player. Probably just rolls the
     * dice.
     *
     * @param player The player making the request (may not be relevant)...
     */
    public void processRollRequest(RocketPlayer player);

    /**
     * This method handles the players decisions based on the roll they got.
     *
     * @param player The player making the request (may not be relevant)...
     * @param response The response (ie decision) that they made. This would be
     * used to update the games status.
     */
    public void processRollResponse(RocketPlayer player, String response);

    /**
     * Returns a string with the current game status (ie scores). This is passed
     * directly back on the tables channel. NOTE: Game Status is not the same as
     * table status. Table status says what it's waiting for. Game status is the
     * scores.
     */
    public String getGameStatus();
}
