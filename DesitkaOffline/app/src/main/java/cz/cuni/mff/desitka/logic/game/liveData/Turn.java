package cz.cuni.mff.desitka.logic.game.liveData;

/**
 * Represents a turn in the game.
 * This class holds the data related to a single turn in the game.
 */
public class Turn {
    final String playerOnMove;
    final int playerRoundScore;
    final int roundPlayers;
    final int gamePlayers;

    /**
     * Constructor for the Turn class.
     * @param playerOnMove The player who is currently on move.
     * @param playerRoundScore The round score of the player.
     * @param roundPlayers The number of players in the round.
     * @param gamePlayers The number of players in the game.
     */
    public Turn(String playerOnMove, int playerRoundScore, int roundPlayers, int gamePlayers) {
        this.playerOnMove = playerOnMove;
        this.playerRoundScore = playerRoundScore;
        this.roundPlayers = roundPlayers;
        this.gamePlayers = gamePlayers;
    }

    /**
     * Returns the player who is currently on move.
     * @return The player who is currently on move.
     */
    public String getRoundPlayer() {
        return playerOnMove;
    }

    /**
     * Returns the number of players in the round.
     * @return The number of players in the round.
     */
    public int getRoundPlayers() {
        return roundPlayers;
    }

    /**
     * Returns the number of players in the game.
     * @return The number of players in the game.
     */
    public int getGamePlayers() {
        return gamePlayers;
    }

    /**
     * Returns the round score of the player.
     * @return The round score of the player.
     */
    public int getPlayerRoundScore() {
        return playerRoundScore;
    }
}