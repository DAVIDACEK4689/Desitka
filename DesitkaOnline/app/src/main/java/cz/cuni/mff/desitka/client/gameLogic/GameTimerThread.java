package cz.cuni.mff.desitka.client.gameLogic;


import static cz.cuni.mff.desitka.JSON.GameStates.GameState.SHOW_ANSWER_WAITING;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;

import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.client.MyAnswer;
import cz.cuni.mff.desitka.JSON.server.Evaluation;
import cz.cuni.mff.desitka.JSON.server.Start;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.GameActivity;
import cz.cuni.mff.desitka.client.activities.StartingActivity;


/**
 * A thread for managing game timers and game state transitions.
 */
public class GameTimerThread extends HandlerThread implements GameStates {
    private final GameViewModel gameViewModel;
    private final Resources resources;
    private CountDownTimer countDownTimer;
    private Handler handler;
    private Context context;

    /**
     * Constructs a new GameTimerThread.
     *
     * @param gameViewModel the game view model
     * @param context the context
     */
    public GameTimerThread(GameViewModel gameViewModel, Context context) {
        super("GameTimerThread");
        this.gameViewModel = gameViewModel;
        this.context = context;
        this.resources = context.getResources();
    }

    /**
     * Returns the timer source ID for the given game state.
     *
     * @param gameState the game state
     * @return the timer source ID
     */
    private int getTimerSource(GameState gameState) {
        switch (gameState) {
            case PLAYER_ANSWERS: return R.string.time_left;
            case SHOW_ANSWER_WAITING: return R.string.round_start;
            case WAITING_FOR_PLAYERS: return R.string.waiting_time;
            case GAME_START: return R.string.game_start;
            case GAME_END: return R.string.time_left;
            default: return R.string.empty_string;
        }
    }

    /**
     * Called when the looper is prepared.
     */
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(getLooper());
    }

    /**
     * Waits until the looper is prepared.
     */
    public void awaitLooperPrepared() {
        while (handler == null) {
            Thread.yield();
        }
    }


    /**
     * Starts a timer for the given game state.
     *
     * @param gameState the game state
     * @param sceneTime the scene time
     */
    private void startTimer(GameState gameState, long sceneTime) {

        countDownTimer = new CountDownTimer(sceneTime, 1000) {
            private final int timerSourceID = getTimerSource(gameState);
            private final String timerSource = resources.getString(timerSourceID);

            @Override
            public void onTick(long millisUntilFinished) {
                gameViewModel.getTimerText().postValue(String.format(timerSource, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                handleTimerFinish(gameState);
            }
        };
        countDownTimer.start();
    }

    /**
     * Handles the finish of a timer for the given game state.
     *
     * @param gameState the game state
     */
    private void handleTimerFinish(GameState gameState) {
        switch (gameState) {
            case GAME_JOINING:
                gameViewModel.reportConnectionError(R.string.server_error);
                break;
            case SHOW_ANSWER_WAITING:
                gameViewModel.reportConnectionError(R.string.server_error);
                break;
            case WAITING_FOR_PLAYERS:
                gameViewModel.reportConnectionError(R.string.players_not_found);
                break;
            case GAME_START:
                showRoundStart();
                break;
            case ROUND_START:
                updateState(GameState.ROUND_PLAYER);
                break;
            case ROUND_PLAYER:
                updateState(GameState.PLAYER_ANSWERS);
                break;
            case PLAYER_ANSWERS:
                dealNoAnswer();
                break;
            case SHOW_ANSWER:
                scheduleNextPlayer();
                break;
            case SHOW_ANSWERS:
                showEvaluation();
                break;
            case ROUND_END:
                showRoundStart();
                break;
            case GAME_END:
                Intent intent = new Intent(context, StartingActivity.class);
                ((GameActivity) context).finish();
                context.startActivity(intent);
                break;
        }
    }

    /**
     * Updates the game state and starts a timer for the new state.
     *
     * @param gameState the new game state
     */
    private void updateState(GameState gameState) {
        gameViewModel.getGameStateChange().postValue(gameState);
        startTimer(gameState, Constants.getSceneTime(gameState));
    }

    /**
     * Cancels the current timer.
     */
    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Deals with the case where no answer was provided.
     */
    private void dealNoAnswer() {
        Turn turn = gameViewModel.getTurn().getValue();
        if (turn.isMyTurn()) {
            MyAnswer myAnswer = new MyAnswer(Constants.PLAYER_PASSES_WITHOUT_ACTIVITY, -1);
            gameViewModel.sendAnswer(GsonParser.toJson(myAnswer));
        }
        updateState(SHOW_ANSWER_WAITING);
    }

    /**
     * Schedules the next player's turn.
     */
    private void scheduleNextPlayer() {
        Turn turn = gameViewModel.getAnswer().getValue().getTurn();
        gameViewModel.getTurn().postValue(turn);

        if (turn.getRoundPlayers() == 0) {
            showAnswers();
            return;
        }
        updateState(GameState.ROUND_PLAYER);
    }

    /**
     * Shows the answers.
     */
    private void showAnswers() {
        if (gameViewModel.waitingForJSON(Evaluation.class)) {
            gameViewModel.reportConnectionError(R.string.server_error);
            return;
        }
        updateState(GameState.SHOW_ANSWERS);
    }

    /**
     * Shows the evaluation.
     */
    private void showEvaluation() {
        Evaluation evaluation = gameViewModel.getEvaluation().getValue();
        if (!evaluation.isGameOver()) {
            updateState(GameState.ROUND_END);
            return;
        }
        updateState(GameState.GAME_END);
    }

    /**
     * Shows the start of a round.
     */
    private void showRoundStart() {
        if (gameViewModel.waitingForJSON(Start.class)) {
            gameViewModel.reportConnectionError(R.string.server_error);
            return;
        }
        updateState(GameState.ROUND_START);
        Start start = gameViewModel.getStart().getValue();
        gameViewModel.getTurn().postValue(start.getTurn());
        gameViewModel.getQuestion().postValue(start.getQuestion());
    }

    /**
     * Posts a state update and starts a timer for the new state.
     *
     * @param gameState the new game state
     * @param sceneTime the scene time
     */
    public void postStateUpdate(GameState gameState, long sceneTime) {
        gameViewModel.getGameStateChange().postValue(gameState);
        handler.post(() -> {
            cancelTimer();
            startTimer(gameState, sceneTime);
        });
    }

    /**
     * Posts a state update with the default scene time for the new state.
     *
     * @param gameState the new game state
     */
    public void postStateUpdate(GameState gameState) {
        long sceneTime = Constants.getSceneTime(gameState);
        postStateUpdate(gameState, sceneTime);
    }

    /**
     * Closes the service.
     */
    public void closeService() {
        handler.post(() -> {
            cancelTimer();
            handler.removeCallbacksAndMessages(null);
            quitSafely();
        });
    }
}
