package cz.cuni.mff.desitka.logic.game.liveData;

/**
 * Represents an evaluation of a game round.
 * This class holds the data related to the evaluation of a round in the game.
 */
public class Evaluation {
    private final PlayerScore[] playerScores;
    private final boolean gameOver;

    /**
     * Constructor for the Evaluation class.
     * @param playerScores The scores of the players.
     * @param gameOver Whether the game is over.
     */
    public Evaluation(PlayerScore[] playerScores, boolean gameOver) {
        this.playerScores = playerScores;
        this.gameOver = gameOver;
    }

    /**
     * Returns the scores of the players.
     * @return The scores of the players.
     */
    public PlayerScore[] getPlayerScores() {
        return playerScores;
    }

    /**
     * Returns whether the game is over.
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Represents a player's score in the game.
     * This class holds the data related to a player's score in the game.
     */
    public static class PlayerScore {
        private final String name;
        private final int score;

        /**
         * Constructor for the PlayerScore class.
         * @param name The name of the player.
         * @param score The score of the player.
         */
        public PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        /**
         * Returns the name of the player.
         * @return The name of the player.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the score of the player.
         * @return The score of the player.
         */
        public int getScore() {
            return score;
        }
    }
}