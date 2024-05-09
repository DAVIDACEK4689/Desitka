package cz.cuni.mff.desitka.logic.game;

/**
 * Represents a player in the game.
 * This class manages the player's name and scores.
 */
public class Player {
    private final String name;
    private int roundScore;
    private int gameScore;

    /**
     * Constructor for the Player class.
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the player.
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the round score of the player.
     * @return The round score of the player.
     */
    public int getRoundScore() {
        return roundScore;
    }

    /**
     * Returns the game score of the player.
     * @return The game score of the player.
     */
    public int getGameScore() {
        return gameScore;
    }

    /**
     * Increases the round score of the player by one.
     */
    public void increaseScore() {
        roundScore++;
    }

    /**
     * Resets the round score of the player to zero.
     */
    public void resetScore() {
        roundScore = 0;
    }

    /**
     * Adds the round score to the game score.
     */
    public void evaluateRoundScore() {
        gameScore += roundScore;
    }
}