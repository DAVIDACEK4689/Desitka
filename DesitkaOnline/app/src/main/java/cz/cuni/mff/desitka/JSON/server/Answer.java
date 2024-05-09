package cz.cuni.mff.desitka.JSON.server;

import cz.cuni.mff.desitka.JSON.JSON;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;

/**
 * The Answer class represents a player's answer in a game round.
 */
public class Answer extends JSON {
    private final int answerID;
    private final String playerName;
    private final int playerAnswerIndex;
    private final int correctAnswerIndex;
    private final Turn turn;

    /**
     * Constructs a new Answer object.
     *
     * @param answerID The ID of the answer.
     * @param playerName The name of the player.
     * @param playerAnswerIndex The index of the player's answer.
     * @param correctAnswerIndex The index of the correct answer.
     * @param turn The turn information.
     */
    public Answer(int answerID, String playerName, int playerAnswerIndex, int correctAnswerIndex, Turn turn) {
        this.answerID = answerID;
        this.playerName = playerName;
        this.playerAnswerIndex = playerAnswerIndex;
        this.correctAnswerIndex = correctAnswerIndex;
        this.turn = turn;
    }

    /**
     * Returns the ID of the answer.
     *
     * @return The ID of the answer.
     */
    public int getAnswerID() {
        return answerID;
    }

    /**
     * Returns the name of the player.
     *
     * @return The name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the index of the player's answer.
     *
     * @return The index of the player's answer.
     */
    public int getPlayerAnswerIndex() {
        return playerAnswerIndex;
    }

    /**
     * Returns the index of the correct answer.
     *
     * @return The index of the correct answer.
     */
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    /**
     * Returns the turn information.
     *
     * @return The turn information.
     */
    public Turn getTurn() {
        return turn;
    }
}