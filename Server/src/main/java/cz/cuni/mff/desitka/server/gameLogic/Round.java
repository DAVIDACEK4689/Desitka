package cz.cuni.mff.desitka.server.gameLogic;

import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.JSON.Question;
import cz.cuni.mff.desitka.JSON.client.MyAnswer;
import cz.cuni.mff.desitka.JSON.server.Answer;
import cz.cuni.mff.desitka.JSON.server.Start;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cz.cuni.mff.desitka.JSON.Constants.PLAYER_PASSES;
import static cz.cuni.mff.desitka.JSON.Constants.playerNoAnswer;

/**
 * This class represents a round in the game.
 */
public class Round {
    private final int roundNumber;
    private int playerIndex;
    private final Game game;
    private final Question question;
    private final List<Player> roundPlayers = new ArrayList<>();

    /**
     * Constructs a new round with a specified round number, list of players, question, and game.
     *
     * @param roundNumber the round number
     * @param roundPlayers the list of players
     * @param question the question for the round
     * @param game the game of the round
     */
    public Round(int roundNumber, List<Player> roundPlayers, Question question, Game game) {
        this.roundNumber = roundNumber;
        this.roundPlayers.addAll(roundPlayers);
        this.question = question;
        this.game = game;
        playerIndex = (roundNumber - 1) % roundPlayers.size();
    }

    /**
     * Returns the current player of the round.
     *
     * @return the current player of the round
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
     * Checks if the round has finished.
     *
     * @return true if the round has finished, false otherwise
     */
    public boolean roundFinished() {
        return roundPlayers.isEmpty();
    }

    /**
     * Starts the round.
     */
    public void start() {
        Question emptyQuestion = createEmptyQuestion(question);
        Turn turn = new Turn(getCurrentPlayer().getName(), roundPlayers.size(), game.getGamePlayers());
        Start start = new Start(roundNumber, emptyQuestion, turn);
        game.sendStart(start);

        long timeout = Constants.getRoundAnswerTime(roundNumber);
        getCurrentPlayer().expectAnswerMessage(timeout);
    }

    /**
     * Creates an empty question based on a specified question.
     *
     * @param question the question to be copied
     * @return the created empty question
     */
    private Question createEmptyQuestion(Question question) {
        Question.SubQuestion[] originalSubQuestions = question.getSubQuestions();
        Question.SubQuestion[] copiedSubQuestions = new Question.SubQuestion[10];

        for (int i = 0; i < 10; i++) {
            Question.SubQuestion originalSubQuestion = originalSubQuestions[i];
            String key = originalSubQuestion.getKey();
            String[] values = Arrays.copyOf(originalSubQuestion.getValues(), 4);
            copiedSubQuestions[i] = new Question.SubQuestion(key, values, -1);
        }
        return new Question(question.getText(), copiedSubQuestions);
    }

    /**
     * Processes the answer of a player.
     *
     * @param myAnswer the answer of the player
     * @param player the player who answered
     */
    public void processAnswer(MyAnswer myAnswer, Player player) {
        Answer answer = parseAnswer(myAnswer, player);
        game.sendAnswer(answer);
        checkRoundEnd();
    }

    /**
     * Parses the answer of a player.
     *
     * @param myAnswer the answer of the player
     * @param player the player who answered
     * @return the parsed answer
     */
    private Answer parseAnswer(MyAnswer myAnswer, Player player) {
        int answerID = myAnswer.getAnswerID();
        if (!playerNoAnswer(answerID)) {
            int playerAnswerIndex = myAnswer.getPlayerAnswerIndex();
            int correctAnswerIndex = question.getSubQuestions()[answerID].getCorrectIndex();
            evaluateAnswer(playerAnswerIndex, correctAnswerIndex, player);
            return new Answer(answerID, player.getName(), playerAnswerIndex, correctAnswerIndex, makeTurn());
        }
        return handleNoAnswer(answerID, player);
    }

    /**
     * Handles the case when a player does not answer.
     *
     * @param answerID the ID of the answer
     * @param player the player who did not answer
     * @return the answer
     */
    private Answer handleNoAnswer(int answerID, Player player) {
        removePlayer();
        checkPossibleNoActivity(answerID, player);
        return new Answer(answerID, player.getName(), -1, -1, makeTurn());
    }

    /**
     * Makes a turn for the next player.
     *
     * @return the turn for the next player
     */
    private Turn makeTurn() {
        return new Turn(nextPlayerName(), roundPlayers.size(), game.getGamePlayers());
    }

    /**
     * Checks if the round has ended and evaluates the round if it has.
     */
    private void checkRoundEnd() {
        if (roundFinished()) {
            game.evaluateRound();
            return;
        }
        long timeout = Constants.getRegularAnswerTime();
        getCurrentPlayer().expectAnswerMessage(timeout);
    }

    /**
     * Returns the name of the next player.
     *
     * @return the name of the next player
     */
    private String nextPlayerName() {
        if (roundFinished()) {
            return null;
        }
        return roundPlayers.get(playerIndex).getName();
    }

    /**
     * Evaluates the answer of a player.
     *
     * @param playerAnswerIndex the index of the player's answer
     * @param correctAnswerIndex the index of the correct answer
     * @param player the player who answered
     */
    private void evaluateAnswer(int playerAnswerIndex, int correctAnswerIndex, Player player) {
        player.resetNoActivity();
        if (playerAnswerIndex == correctAnswerIndex) {
            player.increaseScore();
            movePlayerIndex();
            return;
        }
        player.resetScore();
        removePlayer();
    }

    /**
     * Checks the possible no activity of a player.
     *
     * @param answerID the ID of the answer
     * @param player the player to check
     */
    private void checkPossibleNoActivity(int answerID, Player player) {
        if (answerID == PLAYER_PASSES) {
            player.resetNoActivity();
            return;
        }
        game.checkNoActivity(answerID, player);
    }
}