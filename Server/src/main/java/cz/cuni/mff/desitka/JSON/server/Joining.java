package cz.cuni.mff.desitka.JSON.server;

import cz.cuni.mff.desitka.JSON.JSON;

/**
 * The Joining class represents the result of a player attempting to join a game.
 */
public class Joining extends JSON implements JoiningResults {
    private final JoiningResult joiningResult;
    private final String gameCode;
    private final int playerCount;
    private final long gameFoundation;

    /**
     * Constructs a new Joining object.
     *
     * @param joiningResult The result of the player attempting to join the game.
     * @param gameCode The code of the game.
     * @param playerCount The number of players in the game.
     * @param gameFoundation The foundation of the game.
     */
    public Joining(JoiningResult joiningResult, String gameCode, int playerCount, long gameFoundation) {
        this.joiningResult = joiningResult;
        this.gameCode = gameCode;
        this.playerCount = playerCount;
        this.gameFoundation = gameFoundation;
    }

    /**
     * Returns the result of the player attempting to join the game.
     *
     * @return The result of the player attempting to join the game.
     */
    public JoiningResult getJoiningResult() {
        return joiningResult;
    }

    /**
     * Returns the code of the game.
     *
     * @return The code of the game.
     */
    public String getGameCode() {
        return gameCode;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return The number of players in the game.
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Returns the foundation of the game.
     *
     * @return The foundation of the game.
     */
    public long getGameFoundation() {
        return gameFoundation;
    }
}