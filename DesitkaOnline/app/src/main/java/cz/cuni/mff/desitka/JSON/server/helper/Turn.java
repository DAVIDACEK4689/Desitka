package cz.cuni.mff.desitka.JSON.server.helper;

import cz.cuni.mff.desitka.JSON.JSON;

/**
 * The Turn class represents a turn in a game round.
 */
public class Turn extends JSON {
    final String playerOnMove;
    final int roundPlayers;
    final int gamePlayers;
    boolean myTurn;
    int myScore;

    /**
     * Constructs a new Turn object.
     *
     * @param playerOnMove The player on move.
     * @param roundPlayers The number of players in the round.
     * @param gamePlayers The number of players in the game.
     */
    public Turn(String playerOnMove, int roundPlayers, int gamePlayers) {
        this.playerOnMove = playerOnMove;
        this.roundPlayers = roundPlayers;
        this.gamePlayers = gamePlayers;
    }

    /**
     * Returns the player on move.
     *
     * @return The player on move.
     */
    public String getRoundPlayer() {
        return playerOnMove;
    }

    /**
     * Returns the number of players in the round.
     *
     * @return The number of players in the round.
     */
    public int getRoundPlayers() {
        return roundPlayers;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return The number of players in the game.
     */
    public int getGamePlayers() {
        return gamePlayers;
    }

    /**
     * Returns the score of the player.
     *
     * @return The score of the player.
     */
    public int getMyScore() {
        return myScore;
    }

    /**
     * Sets the score of the player.
     *
     * @param score The score of the player.
     */
    public void setMyScore(int score) {
        myScore = score;
    }

    /**
     * Checks if it is the player's turn.
     *
     * @return true if it is the player's turn, false otherwise.
     */
    public boolean isMyTurn() {
        return myTurn;
    }

    /**
     * Sets if it is the player's turn.
     *
     * @param turn true if it is the player's turn, false otherwise.
     */
    public void setMyTurn(boolean turn) {
        myTurn = turn;
    }
}