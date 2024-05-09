package cz.cuni.mff.desitka.logic.game.liveData;

/**
 * Represents the start of a game round.
 * This class holds the data related to the start of a round in the game.
 */
public class Start {
    private final int roundNumber;
    private final Turn turn;

    /**
     * Constructor for the Start class.
     * @param roundNumber The number of the round.
     * @param turn The turn information.
     */
    public Start(int roundNumber, Turn turn) {
        this.roundNumber = roundNumber;
        this.turn = turn;
    }

    /**
     * Returns the turn information.
     * @return The turn information.
     */
    public Turn getTurn() {
        return turn;
    }

    /**
     * Returns the number of the round.
     * @return The number of the round.
     */
    public int getRoundNumber() {
        return roundNumber;
    }
}