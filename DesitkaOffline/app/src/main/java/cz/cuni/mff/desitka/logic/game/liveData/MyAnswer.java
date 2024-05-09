package cz.cuni.mff.desitka.logic.game.liveData;

/**
 * Represents a player's answer in the game.
 * This class holds the data related to a player's answer in the game.
 */
public class MyAnswer {
    private final int answerID;
    private final int playerAnswerIndex;

    /**
     * Constructor for the MyAnswer class.
     * @param answerID The ID of the answer.
     * @param playerAnswerIndex The index of the player's answer.
     */
    public MyAnswer(int answerID, int playerAnswerIndex) {
        this.answerID = answerID;
        this.playerAnswerIndex = playerAnswerIndex;
    }

    /**
     * Returns the ID of the answer.
     * @return The ID of the answer.
     */
    public int getAnswerID() {
        return answerID;
    }

    /**
     * Returns the index of the player's answer.
     * @return The index of the player's answer.
     */
    public int getPlayerAnswerIndex() {
        return playerAnswerIndex;
    }
}