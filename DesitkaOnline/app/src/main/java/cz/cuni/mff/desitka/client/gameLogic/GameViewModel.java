package cz.cuni.mff.desitka.client.gameLogic;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cz.cuni.mff.desitka.JSON.*;
import cz.cuni.mff.desitka.JSON.Question;
import cz.cuni.mff.desitka.JSON.server.*;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * ViewModel for the game, managing the game state and interactions with the GameModel.
 */
public class GameViewModel extends ViewModel implements JoiningResults, GameStates {
    private GameModel gameModel;
    private GameState displayedGameState = GameState.GAME_JOINING;
    private final MutableLiveData<Joining> joining = new MutableLiveData<>();
    private final MutableLiveData<Waiting> waiting = new MutableLiveData<>();
    private final MutableLiveData<Start> start = new MutableLiveData<>();
    private final MutableLiveData<Question> question = new MutableLiveData<>();
    private final MutableLiveData<Turn> turn = new MutableLiveData<>();
    private final MutableLiveData<Answer> answer = new MutableLiveData<>();
    private final MutableLiveData<Evaluation> evaluation = new MutableLiveData<>();
    private final MutableLiveData<GameState> gameStateChange = new MutableLiveData<>();
    private final MutableLiveData<String> timerText = new MutableLiveData<>();
    private final MutableLiveData<Integer> questionDetailID = new MutableLiveData<>();
    private final MutableLiveData<Integer> connectionError = new MutableLiveData<>(-1);

    /**
     * Returns the MutableLiveData object for the joining state.
     *
     * @return the MutableLiveData object for the joining state
     */
    public MutableLiveData<Joining> getJoining() {
        return joining;
    }

    /**
     * Returns the MutableLiveData object for the waiting state.
     *
     * @return the MutableLiveData object for the waiting state
     */
    public MutableLiveData<Waiting> getWaiting() {
        return waiting;
    }

    /**
     * Returns the MutableLiveData object for the start state.
     *
     * @return the MutableLiveData object for the start state
     */
    public MutableLiveData<Start> getStart() {
        return start;
    }

    /**
     * Returns the MutableLiveData object for the question state.
     *
     * @return the MutableLiveData object for the question state
     */
    public MutableLiveData<Question> getQuestion() {
        return question;
    }

    /**
     * Returns the MutableLiveData object for the turn state.
     *
     * @return the MutableLiveData object for the turn state
     */
    public MutableLiveData<Turn> getTurn() {
        return turn;
    }

    /**
     * Returns the MutableLiveData object for the answer state.
     *
     * @return the MutableLiveData object for the answer state
     */
    public MutableLiveData<Answer> getAnswer() {
        return answer;
    }

    /**
     * Returns the MutableLiveData object for the evaluation state.
     *
     * @return the MutableLiveData object for the evaluation state
     */
    public MutableLiveData<Evaluation> getEvaluation() {
        return evaluation;
    }

    /**
     * Returns the MutableLiveData object for the connection error state.
     *
     * @return the MutableLiveData object for the connection error state
     */
    public MutableLiveData<Integer> getConnectionError() {
        return connectionError;
    }

    /**
     * Returns the MutableLiveData object for the question detail ID state.
     *
     * @return the MutableLiveData object for the question detail ID state
     */
    public MutableLiveData<Integer> getQuestionDetailID() {
        return questionDetailID;
    }

    /**
     * Returns the MutableLiveData object for the game state change.
     *
     * @return the MutableLiveData object for the game state change
     */
    public MutableLiveData<GameState> getGameStateChange() {
        return gameStateChange;
    }

    /**
     * Returns the MutableLiveData object for the timer text.
     *
     * @return the MutableLiveData object for the timer text
     */
    public MutableLiveData<String> getTimerText() {
        return timerText;
    }

    /**
     * Initializes the GameModel with the server address and port from shared preferences.
     *
     * @param context the context
     */
    public void initializeGameModel(Context context) {
        String url = Preferences.getServerAddress(context);
        int port = Preferences.getServerPort(context);
        gameModel = new GameModel(this, url, port, context);
    }

    /**
     * Starts the game with the joining information from the intent.
     *
     * @param intent the intent containing the joining information
     */
    public void startGame(Intent intent) {
        gameModel.startGame(intent.getStringExtra("myJoining"));
    }

    /**
     * Sends the player's answer to the GameModel.
     *
     * @param answer the player's answer
     */
    public void sendAnswer(String answer) {
        gameModel.sendAnswer(answer);
    }

    /**
     * Closes the GameModel when the ViewModel is cleared.
     */
    @Override
    public void onCleared() {
        super.onCleared();
        closeGameModel();
    }

    /**
     * Closes the GameModel.
     */
    private void closeGameModel() {
        if (gameModel != null) {
            gameModel.close();
        }
    }

    /**
     * Returns the currently displayed game state.
     *
     * @return the currently displayed game state
     */
    public GameState getDisplayedGameState() {
        return displayedGameState;
    }

    /**
     * Updates the displayed game state.
     *
     * @param newState the new game state
     */
    public void updateGameState(GameState newState) {
        displayedGameState = newState;
    }

    /**
     * Checks if the GameModel is waiting for a specific type of JSON.
     *
     * @param json the class of the JSON to check for
     * @return true if the GameModel is waiting for the specified JSON, false otherwise
     */
    public boolean waitingForJSON(Class<? extends JSON> json) {
        return gameModel.getExpectedJSON() == json;
    }

    /**
     * Reports a connection error.
     *
     * @param errorId the ID of the error
     */
    public void reportConnectionError(int errorId) {
        if (connectionError.getValue() == -1) {
            connectionError.postValue(errorId);
        }
    }
}