package cz.cuni.mff.desitka.JSON.server;

import cz.cuni.mff.desitka.JSON.JSON;
import cz.cuni.mff.desitka.JSON.Question;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;

/**
 * The Start class represents the start of a round in the game.
 */
public class Start extends JSON {
    private final int roundNumber;
    private final Question question;
    private final Turn turn;

    /**
     * Constructs a new Start object.
     *
     * @param roundNumber The round number.
     * @param question The question for the round.
     * @param turn The turn information.
     */
    public Start(int roundNumber, Question question, Turn turn) {
        this.roundNumber = roundNumber;
        this.turn = turn;
        this.question = question;
    }

    /**
     * Returns the question for the round.
     *
     * @return The question for the round.
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Returns the turn information.
     *
     * @return The turn information.
     */
    public Turn getTurn() {
        return turn;
    }

    /**
     * Returns the round number.
     *
     * @return The round number.
     */
    public int getRoundNumber() {
        return roundNumber;
    }
}