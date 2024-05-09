package cz.cuni.mff.desitka.logic.game;

import static cz.cuni.mff.desitka.interfaces.GameStates.GameState.*;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cz.cuni.mff.desitka.QuestionParser;
import cz.cuni.mff.desitka.interfaces.GameStates;
import cz.cuni.mff.desitka.Question;
import cz.cuni.mff.desitka.logic.game.liveData.Answer;
import cz.cuni.mff.desitka.logic.game.liveData.Evaluation;
import cz.cuni.mff.desitka.logic.game.liveData.MyAnswer;
import cz.cuni.mff.desitka.logic.game.liveData.Start;

/**
 * Controller for game logic.
 * This class manages the game functionality of the application.
 */
public class GameController implements GameStates {
    private final GameViewModel gameViewModel;
    private final Context context;
    private final int MAX_SCORE = 20;
    private final List<Question> questions;
    private List<Player> players = new ArrayList<>();
    private int roundNumber;
    private Round round;
    private Question question;

    /**
     * Constructor for the GameController class.
     * @param gameViewModel The ViewModel for game logic.
     * @param context The application context.
     */
    public GameController(GameViewModel gameViewModel, Context context) {
        this.gameViewModel = gameViewModel;
        this.context = context;
        questions = QuestionParser.getQuestions(context);
    }

    /**
     * Chooses a question randomly from the list of questions.
     * If the list of questions is empty, it is refilled with questions parsed from the context.
     * @return The chosen question.
     */
    private Question chooseQuestion() {
        if (questions.isEmpty()) {
            questions.addAll(QuestionParser.getQuestions(context));
        }
        int randomIndex = (int) (Math.random() * questions.size());
        return questions.remove(randomIndex);
    }

    /**
     * Checks if the game is over.
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return players.stream().anyMatch(player -> player.getGameScore() >= MAX_SCORE);
    }

    /**
     * Starts a round of the game.
     */
    public void startRound() {
        players.forEach(Player::resetScore);
        question = chooseQuestion();
        round = new Round(++roundNumber, players, question, this);
        round.start();
    }

    /**
     * Creates an empty question based on the given question.
     * @param question The question to base the empty question on.
     * @return The created empty question.
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
     * Processes the given answer.
     * @param myAnswer The answer to process.
     */
    public void processAnswer(MyAnswer myAnswer) {
        round.processAnswer(myAnswer);
    }

    /**
     * Evaluates the round.
     */
    public void evaluateRound() {
        players.forEach(Player::evaluateRoundScore);
        Evaluation evaluation = new Evaluation(getPlayerScore(), isGameOver());
        gameViewModel.getEvaluation().postValue(evaluation);
        gameViewModel.getQuestion().postValue(question);
    }

    /**
     * Returns the scores of the players.
     * @return An array of PlayerScore objects representing the scores of the players.
     */
    private Evaluation.PlayerScore[] getPlayerScore() {
        return players.stream()
                .sorted((p1, p2) -> p2.getGameScore() - p1.getGameScore())
                .map(player -> new Evaluation.PlayerScore(player.getName(), player.getGameScore()))
                .toArray(Evaluation.PlayerScore[]::new);
    }

    /**
     * Returns the number of players in the game.
     * @return The number of players in the game.
     */
    public int getGamePlayers() {
        return players.size();
    }

    /**
     * Sends the start of the game to the ViewModel.
     * @param start The start of the game.
     */
    public void sendStart(Start start) {
        gameViewModel.getStart().postValue(start);
        gameViewModel.getQuestion().postValue(createEmptyQuestion(question));
        gameViewModel.getTurn().postValue(start.getTurn());
    }

    /**
     * Sends the answer to the ViewModel.
     * @param answer The answer to send.
     */
    public void sendAnswer(Answer answer) {
        if (answer.getAnswerID() != -1) {
            int correctIndex = answer.getCorrectAnswerIndex();
            Question displayedQuestion = gameViewModel.getQuestion().getValue();
            displayedQuestion.getSubQuestions()[answer.getAnswerID()].setCorrectIndex(correctIndex);
            gameViewModel.getQuestion().postValue(displayedQuestion);
        }
        gameViewModel.getAnswer().postValue(answer);
        gameViewModel.getTurn().postValue(answer.getTurn());
    }

    /**
     * Starts the game with the given player names.
     * @param playerNames The names of the players.
     */
    public void startGame(String[] playerNames) {
        players = Arrays.stream(playerNames)
                .map(Player::new)
                .collect(Collectors.toList());
        startRound();
    }

    /**
     * Handles the finish of the timer.
     * @param value The current game state.
     */
    public void handleTimerFinish(GameState value) {
        switch (value) {
            case GAME_START:
                updateState(ROUND_START);
                break;
            case ROUND_START:
                updateState(ROUND_PLAYER);
                break;
            case ROUND_PLAYER:
                updateState(PLAYER_ANSWERS);
                break;
            case SHOW_ANSWER:
                handleShowAnswer();
                break;
            case SHOW_ANSWERS:
                handleShowAnswers();
                break;
            case ROUND_END:
                updateState(ROUND_START);
                break;
        }
    }

    /**
     * Handles the show answer state.
     */
    private void handleShowAnswer() {
        if (round.roundFinished()) {
            updateState(SHOW_ANSWERS);
            return;
        }
        updateState(ROUND_PLAYER);
    }

    /**
     * Handles the show answers state.
     */
    private void handleShowAnswers() {
        if (isGameOver()) {
            updateState(GAME_END);
            return;
        }
        updateState(ROUND_END);
        startRound();
    }

    /**
     * Updates the game state.
     * @param gameState The game state to update to.
     */
    private void updateState(GameState gameState) {
        gameViewModel.getGameFragmentChange().postValue(gameState);
    }

    /**
     * Returns the names of the players.
     * @return An array of the names of the players.
     */
    public String[] getPlayerNames() {
        return players.stream()
                .map(Player::getName)
                .toArray(String[]::new);
    }
}