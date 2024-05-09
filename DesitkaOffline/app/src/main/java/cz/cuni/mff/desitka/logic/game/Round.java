package cz.cuni.mff.desitka.logic.game;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.desitka.logic.game.liveData.Answer;
import cz.cuni.mff.desitka.logic.game.liveData.MyAnswer;
import cz.cuni.mff.desitka.logic.game.liveData.Start;
import cz.cuni.mff.desitka.logic.game.liveData.Turn;
import cz.cuni.mff.desitka.Question;

/**
 * Represents a round in the game.
 * This class manages the logic for a single round of the game.
 */
public class Round {
    private final int roundNumber;
    private int playerIndex;
    private final GameController gameController;
    private final Question question;
    private final List<Player> roundPlayers = new ArrayList<>();
    private int answeredQuestions = 0;

    /**
     * Constructor for the Round class.
     * @param roundNumber The number of the round.
     * @param roundPlayers The players participating in the round.
     * @param question The question for the round.
     * @param gameController The game controller.
     */
    public Round(int roundNumber, List<Player> roundPlayers, Question question, GameController gameController) {
        this.roundNumber = roundNumber;
        this.roundPlayers.addAll(roundPlayers);
        this.question = question;
        this.gameController = gameController;
        playerIndex = (roundNumber - 1) % roundPlayers.size();
    }

    /**
     * Returns the current player.
     * @return The current player.
     */
    private Player getCurrentPlayer() {
        return roundPlayers.get(playerIndex);
    }

    /**
     * Moves the player index to the next player.
     */
    private void movePlayerIndex() {
        playerIndex = (playerIndex + 1) % roundPlayers.size();
    }

    /**
     * Removes the current player from the round.
     */
    private void removePlayer() {
        roundPlayers.remove(playerIndex);
        if (playerIndex == roundPlayers.size()) {
            playerIndex = 0;
        }
    }

    /**
     * Checks if the round is finished.
     * @return True if the round is finished, false otherwise.
     */
    public boolean roundFinished() {
        return roundPlayers.isEmpty() || answeredQuestions == 10;
    }

    /**
     * Starts the round.
     */
    public void start() {
        Player player = getCurrentPlayer();
        Turn turn = new Turn(player.getName(), player.getRoundScore(), roundPlayers.size(), gameController.getGamePlayers());
        Start start = new Start(roundNumber, turn);
        gameController.sendStart(start);
    }

    /**
     * Processes the answer provided by the player.
     * @param myAnswer The answer provided by the player.
     */
    public void processAnswer(MyAnswer myAnswer) {
        Answer answer = parseAnswer(myAnswer, getCurrentPlayer());
        gameController.sendAnswer(answer);
        checkRoundEnd();
    }

    /**
     * Parses the answer provided by the player.
     * @param myAnswer The answer provided by the player.
     * @param player The player who provided the answer.
     * @return The parsed answer.
     */
    private Answer parseAnswer(MyAnswer myAnswer, Player player) {
        int answerID = myAnswer.getAnswerID();
        if (answerID != -1) {
            answeredQuestions++;
            int playerAnswer = myAnswer.getPlayerAnswerIndex();
            int correctAnswer = question.getSubQuestions()[answerID].getCorrectIndex();
            evaluateAnswer(playerAnswer, correctAnswer, player);
            return new Answer(answerID, player.getName(), playerAnswer, correctAnswer, makeTurn());
        }
        removePlayer();
        return new Answer(answerID, player.getName(), -1, -1, makeTurn());
    }

    /**
     * Makes a turn.
     * @return The turn.
     */
    private Turn makeTurn() {
        if (roundFinished()) {
            return new Turn(null, 0, 0, gameController.getGamePlayers());
        }
        Player nextPlayer = roundPlayers.get(playerIndex);
        return new Turn(nextPlayer.getName(), nextPlayer.getRoundScore(), roundPlayers.size(), gameController.getGamePlayers());
    }

    /**
     * Checks if the round has ended and if so, evaluates the round.
     */
    private void checkRoundEnd() {
        if (roundFinished()) {
            gameController.evaluateRound();
        }
    }

    /**
     * Evaluates the answer provided by the player.
     * @param playerAnswer The answer provided by the player.
     * @param correctAnswer The correct answer.
     * @param player The player who provided the answer.
     */
    private void evaluateAnswer(int playerAnswer, int correctAnswer, Player player) {
        if (playerAnswer == correctAnswer) {
            player.increaseScore();
            movePlayerIndex();
            return;
        }
        player.resetScore();
        removePlayer();
    }
}