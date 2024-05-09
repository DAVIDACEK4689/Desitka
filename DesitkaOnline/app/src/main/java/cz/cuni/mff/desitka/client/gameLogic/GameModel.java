package cz.cuni.mff.desitka.client.gameLogic;

import android.content.Context;

import com.google.gson.JsonSyntaxException;

import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.JSON;
import cz.cuni.mff.desitka.JSON.Question;
import cz.cuni.mff.desitka.JSON.client.*;
import cz.cuni.mff.desitka.JSON.server.*;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.communication.*;


/**
 * The GameModel class handles the game logic and communication with the server.
 */
public class GameModel implements GameStates, MyJoiningRequests, JoiningResults {
    private final GameViewModel gameViewModel;
    private final GameTimerThread gameTimerThread;
    private final CommunicationService communicationService;
    private String message = "";
    private JSON objectJSON;
    private Class<? extends JSON> expectedJSON = Joining.class;


    /**
     * Constructs a new GameModel.
     *
     * @param gameViewModel the game view model
     * @param url the server URL
     * @param port the server port
     * @param context the context
     */
    public GameModel(GameViewModel gameViewModel, String url, int port, Context context) {
        this.gameViewModel = gameViewModel;
        gameTimerThread = new GameTimerThread(gameViewModel, context);
        communicationService = new CommunicationService(url, port);
        setListener();
    }


    /**
     * Sets the communication listener for the communication service.
     */
    private void setListener() {
        communicationService.setCommunicationListener(new CommunicationListener() {
            @Override
            public void startGameTimer() {
                gameTimerThread.awaitLooperPrepared();
                gameTimerThread.postStateUpdate(GameState.GAME_JOINING);
            }

            @Override
            public void cancelGameTimer() {
                gameTimerThread.closeService();
            }

            @Override
            public void onDataReceived(String message) {
                if (message.equals("Connection error")) {
                    gameViewModel.reportConnectionError(R.string.server_error);
                    return;
                }

                if (validMessage(message)) {
                    processJSON();
                }
            }
        });
    }

    /**
     * Processes the received JSON message.
     */
    private void processJSON() {
        switch (expectedJSON.getSimpleName()) {
            case "Joining":
                processJoining((Joining) objectJSON);
                break;
            case "Waiting":
                processWaiting((Waiting) objectJSON);
                break;
            case "Start":
                processStart((Start) objectJSON);
                break;
            case "Answer":
                processAnswer((Answer) objectJSON);
                break;
            case "Evaluation":
                processEvaluation((Evaluation) objectJSON);
                break;
        }
    }

    /**
     * Processes the joining message.
     *
     * @param joining the joining message
     */
    private void processJoining(Joining joining) {
        expectedJSON = Waiting.class;
        gameViewModel.getJoining().postValue(joining);

        switch (joining.getJoiningResult()) {
            case GAME_NOT_FOUND:
                gameViewModel.reportConnectionError(R.string.game_not_found);
                break;
            case NAME_ALREADY_JOINED:
                gameViewModel.reportConnectionError(R.string.name_already_joined);
                break;
            case JOINED:
                successJoining(joining.getGameFoundation());
                break;
        }
    }

    /**
     * Handles the successful joining of a game.
     *
     * @param gameFoundation the game foundation
     */
    private void successJoining(long gameFoundation) {
        long currentTime = System.currentTimeMillis();
        long timeForJoin = Constants.WAITING_TIME + gameFoundation - currentTime;
        gameTimerThread.postStateUpdate(GameState.WAITING_FOR_PLAYERS, timeForJoin);
    }

    /**
     * Processes the waiting message.
     *
     * @param waiting the waiting message
     */
    private void processWaiting(Waiting waiting) {
        gameViewModel.getWaiting().postValue(waiting);

        if (waiting.startReady()) {
            expectedJSON = Start.class;
            gameTimerThread.postStateUpdate(GameState.GAME_START);
        }
    }

    /**
     * Processes the start message.
     *
     * @param start the start message
     */
    private void processStart(Start start) {
        gameViewModel.getStart().postValue(start);
        expectedJSON = Answer.class;
    }

    /**
     * Returns the expected JSON class.
     *
     * @return the expected JSON class
     */
    public Class<? extends JSON> getExpectedJSON() {
        return expectedJSON;
    }


    /**
     * Processes the answer message.
     *
     * @param answer the answer message
     */
    private void processAnswer(Answer answer) {
        updateQuestion(answer.getAnswerID(), answer.getCorrectAnswerIndex());
        gameViewModel.getAnswer().postValue(answer);
        gameTimerThread.postStateUpdate(GameState.SHOW_ANSWER);

        if (answer.getTurn().getRoundPlayers() == 0) {
            expectedJSON = Evaluation.class;
        }
    }

    /**
     * Updates the question with the answer ID and correct answer index.
     *
     * @param answerID the answer ID
     * @param correctAnswerIndex the correct answer index
     */
    private void updateQuestion(int answerID, int correctAnswerIndex) {
        if (!Constants.playerNoAnswer(answerID)) {
            Question question = gameViewModel.getQuestion().getValue();
            question.getSubQuestions()[answerID].setCorrectIndex(correctAnswerIndex);
            gameViewModel.getQuestion().postValue(question);
        }
    }

    /**
     * Processes the evaluation message.
     *
     * @param evaluation the evaluation message
     */
    private void processEvaluation(Evaluation evaluation) {
        gameViewModel.getEvaluation().postValue(evaluation);
        gameViewModel.getQuestion().postValue(evaluation.getSolvedQuestion());
        expectedJSON = Start.class;
    }

    /**
     * Checks if the received message is valid.
     *
     * @param newMessage the received message
     * @return true if the message is valid, false otherwise
     */
    private boolean validMessage(String newMessage) {
        try {
            objectJSON = GsonParser.fromJson(message + newMessage, expectedJSON);
            message = "";
            return true;
        }
        catch (JsonSyntaxException e) {
            // message is not complete
            message += newMessage;
            return false;
        }
    }

    /**
     * Starts the game with the given joining message.
     *
     * @param myJoining the joining message
     */
    public void startGame(String myJoining) {
        gameTimerThread.start();
        communicationService.start();
        communicationService.sendMessage(myJoining);
    }

    /**
     * Sends the answer to the server.
     *
     * @param answer the answer
     */
    public void sendAnswer(String answer) {
        communicationService.sendMessage(answer);
    }

    /**
     * Closes the game model.
     */
    public void close() {
        gameTimerThread.closeService();
        communicationService.closeService();
    }
}