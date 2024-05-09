package cz.cuni.mff.desitka.logic.game;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cz.cuni.mff.desitka.interfaces.GameStates;
import cz.cuni.mff.desitka.logic.game.liveData.Answer;
import cz.cuni.mff.desitka.logic.game.liveData.Evaluation;
import cz.cuni.mff.desitka.logic.game.liveData.MyAnswer;
import cz.cuni.mff.desitka.logic.game.liveData.Start;
import cz.cuni.mff.desitka.logic.game.liveData.Turn;
import cz.cuni.mff.desitka.Question;

/**
 * ViewModel for game logic.
 * This class holds the data and methods related to the game functionality of the application.
 */
public class GameViewModel extends ViewModel implements GameStates {
    private GameController gameController;
    private final MutableLiveData<Start> start = new MutableLiveData<>();
    private final MutableLiveData<Question> question = new MutableLiveData<>();
    private final MutableLiveData<Turn> turn = new MutableLiveData<>();
    private final MutableLiveData<Answer> answer = new MutableLiveData<>();
    private final MutableLiveData<Evaluation> evaluation = new MutableLiveData<>();
    private final MutableLiveData<Integer> questionDetailID = new MutableLiveData<>();
    private final MutableLiveData<GameState> displayedGameFragment = new MutableLiveData<>(GameState.GAME_START);
    private final MutableLiveData<GameState> gameFragmentChange = new MutableLiveData<>();
    private long sceneTime = 0;

    /**
     * Returns the start of the game.
     * @return A MutableLiveData holding the start of the game.
     */
    public MutableLiveData<Start> getStart() {
        return start;
    }

    /**
     * Returns the question of the game.
     * @return A MutableLiveData holding the question of the game.
     */
    public MutableLiveData<Question> getQuestion() {
        return question;
    }

    /**
     * Returns the turn of the game.
     * @return A MutableLiveData holding the turn of the game.
     */
    public MutableLiveData<Turn> getTurn() {
        return turn;
    }

    /**
     * Returns the answer of the game.
     * @return A MutableLiveData holding the answer of the game.
     */
    public MutableLiveData<Answer> getAnswer() {
        return answer;
    }

    /**
     * Returns the evaluation of the game.
     * @return A MutableLiveData holding the evaluation of the game.
     */
    public MutableLiveData<Evaluation> getEvaluation() {
        return evaluation;
    }

    /**
     * Returns the question detail ID.
     * @return A MutableLiveData holding the question detail ID.
     */
    public MutableLiveData<Integer> getQuestionDetailID() {
        return questionDetailID;
    }

    /**
     * Returns the game fragment change.
     * @return A MutableLiveData holding the game fragment change.
     */
    public MutableLiveData<GameState> getGameFragmentChange() {
        return gameFragmentChange;
    }

    /**
     * Returns the displayed game fragment.
     * @return A MutableLiveData holding the displayed game fragment.
     */
    public MutableLiveData<GameState> getDisplayedGameFragment() {
        return displayedGameFragment;
    }

    /**
     * Starts the game with the given intent.
     * @param intent The intent to start the game with.
     */
    public void startGame(Intent intent) {
        String[] playerNames = intent.getStringArrayExtra("playerNames");
        gameController.startGame(playerNames);
    }

    /**
     * Processes the given answer.
     * @param answer The answer to process.
     */
    public void processAnswer(int answer) {
        MyAnswer myAnswer = new MyAnswer(questionDetailID.getValue(), answer);
        gameController.processAnswer(myAnswer);
    }

    /**
     * Initializes the game controller with the given context.
     * @param context The context to initialize the game controller with.
     */
    public void initializeGameController(Context context) {
        gameController = new GameController(this, context);
    }

    /**
     * Returns the scene time.
     * @return The scene time.
     */
    public long getSceneTime() {
        return sceneTime;
    }

    /**
     * Sets the scene time.
     * @param sceneTime The scene time to set.
     */
    public void setSceneTime(long sceneTime) {
        this.sceneTime = sceneTime;
    }

    /**
     * Handles the finish of the timer.
     */
    public void handleTimerFinish() {
        gameController.handleTimerFinish(displayedGameFragment.getValue());
    }

    /**
     * Returns the names of the players.
     * @return The names of the players.
     */
    public String[] getPlayerNames() {
        return gameController.getPlayerNames();
    }
}