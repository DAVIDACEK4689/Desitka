package cz.cuni.mff.desitka.JSON.client;

import cz.cuni.mff.desitka.JSON.JSON;

/**
 * The MyAnswer class represents a player's answer in a game round.
 */
public class MyAnswer extends JSON {
    private final int answerID;
    private final int playerAnswerIndex;

    /**
     * Constructs a new MyAnswer object.
     *
     * @param answerID The ID of the answer.
     * @param playerAnswerIndex The index of the player's answer.
     */
    public MyAnswer(int answerID, int playerAnswerIndex) {
        this.answerID = answerID;
        this.playerAnswerIndex = playerAnswerIndex;
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
     * Returns the index of the player's answer.
     *
     * @return The index of the player's answer.
     */
    public int getPlayerAnswerIndex() {
        return playerAnswerIndex;
    }
}