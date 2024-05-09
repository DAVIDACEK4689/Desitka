package cz.cuni.mff.desitka.JSON.server;

import cz.cuni.mff.desitka.JSON.JSON;
import cz.cuni.mff.desitka.JSON.Question;

/**
 * The Evaluation class represents the evaluation of a game round.
 */
public class Evaluation extends JSON {
    private final Question solvedQuestion;
    private final PlayerScore[] playerScores;
    private final boolean gameOver;

    /**
     * Constructs a new Evaluation object.
     *
     * @param solvedQuestion The question that was solved in the round.
     * @param playerScores The scores of the players.
     * @param gameOver Indicates if the game is over.
     */
    public Evaluation(Question solvedQuestion, PlayerScore[] playerScores, boolean gameOver) {
        this.solvedQuestion = solvedQuestion;
        this.playerScores = playerScores;
        this.gameOver = gameOver;
    }

    /**
     * Returns the question that was solved in the round.
     *
     * @return The question that was solved in the round.
     */
    public Question getSolvedQuestion() {
        return solvedQuestion;
    }

    /**
     * Returns the scores of the players.
     *
     * @return The scores of the players.
     */
    public PlayerScore[] getPlayerScores() {
        return playerScores;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * The PlayerScore class represents the score of a player.
     */
    public static class PlayerScore {
        private final String name;
        private final int score;

        /**
         * Constructs a new PlayerScore object.
         *
         * @param name The name of the player.
         * @param score The score of the player.
         */
        public PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        /**
         * Returns the name of the player.
         *
         * @return The name of the player.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the score of the player.
         *
         * @return The score of the player.
         */
        public int getScore() {
            return score;
        }
    }
}