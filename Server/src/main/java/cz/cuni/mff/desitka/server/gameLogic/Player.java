package cz.cuni.mff.desitka.server.gameLogic;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.JSON;
import cz.cuni.mff.desitka.JSON.client.MyAnswer;
import cz.cuni.mff.desitka.JSON.client.MyJoining;
import cz.cuni.mff.desitka.JSON.client.MyJoiningRequests;
import cz.cuni.mff.desitka.JSON.server.Joining;
import cz.cuni.mff.desitka.JSON.server.JoiningResults;
import cz.cuni.mff.desitka.server.GameManager;
import cz.cuni.mff.desitka.server.communication.CommunicationService;

import java.net.Socket;

/**
 * This class represents a player in the server.
 */
public class Player implements JoiningResults, MyJoiningRequests {
    private String name;
    private String message = "";
    private int roundScore;
    private int gameScore;
    private int noActivity = 0;
    private final CommunicationService communicationService;

    /**
     * Constructs a new player with a specified socket and game manager.
     *
     * @param socket the socket of the player
     * @param gameManager the game manager of the server
     */
    public Player(Socket socket, GameManager gameManager) {
        communicationService = new CommunicationService(socket);
        setJoiningListener(gameManager);
        communicationService.start();
        communicationService.expectMessage(0);
    }

    /**
     * Sets the joining listener for the player.
     *
     * @param gameManager the game manager of the server
     */
    private void setJoiningListener(GameManager gameManager) {
        communicationService.setListener(data -> {
            if (!data.equals("Connection error")) {
                try {
                    MyJoining myJoining = GsonParser.fromJson(message + data, MyJoining.class);
                    validateJoining(myJoining);
                    processJoining(gameManager, myJoining);
                }
                catch (JsonSyntaxException e) {
                    // message is not complete
                    message += data;
                }
                catch (JsonParseException e) {
                    // message is not valid
                    communicationService.close();
                }
            }
        });
    }

    /**
     * Processes the joining of the player.
     *
     * @param gameManager the game manager of the server
     * @param myJoining the joining request of the player
     */
    private void processJoining(GameManager gameManager, MyJoining myJoining) {
        message = "";
        communicationService.confirmMessageReceive();

        name = myJoining.getPlayerName();
        gameManager.connectPlayer(myJoining, this);
    }

    /**
     * Validates the joining request of the player.
     *
     * @param myJoining the joining request of the player
     */
    private void validateJoining(MyJoining myJoining) {
        if (invalidName(myJoining.getPlayerName()) || invalidPlayerCount(myJoining)) {
            throw new JsonParseException("Invalid name");
        }
    }

    /**
     * Checks if the player count in the joining request is invalid.
     *
     * @param myJoining the joining request of the player
     * @return true if the player count is invalid, false otherwise
     */
    private boolean invalidPlayerCount(MyJoining myJoining) {
        RequestType requestType = myJoining.getRequestType();
        int playerCount = myJoining.getPlayerCount();
        return requestType == RequestType.CREATE_FRIEND_GAME && (playerCount < 2 || playerCount > 5);
    }

    /**
     * Checks if the name of the player is invalid.
     *
     * @param playerName the name of the player
     * @return true if the name is invalid, false otherwise
     */
    private boolean invalidName(String playerName) {
        return playerName == null || playerName.isEmpty();
    }

    /**
     * Sets the game for the player.
     *
     * @param game the game to be set
     */
    public void setGame(Game game) {
        sendMessage(new Joining(JoiningResult.JOINED, game.getGameCode(), game.PLAYER_COUNT(), game.getCreationTime()));
        setAnswerListener(game);
    }

    /**
     * Sets the answer listener for the player.
     *
     * @param game the game of the player
     */
    private void setAnswerListener(Game game) {
        communicationService.setListener(data -> {
            if (data.equals("Connection error")) {
                game.processAnswer(new MyAnswer(Constants.PLAYER_DISCONNECTED, -1), this);
            }
            parseAnswer(game, data);
        });
    }

    /**
     * Parses the answer of the player.
     *
     * @param game the game of the player
     * @param data the data to be parsed
     */
    private void parseAnswer(Game game, String data) {
        try {
            MyAnswer myAnswer = GsonParser.fromJson(message + data, MyAnswer.class);
            validateAnswer(myAnswer);
            processAnswer(game, myAnswer);
        }
        catch (JsonSyntaxException e) {
            // message is not complete
            message += data;
        }
        catch (JsonParseException e) {
            // message is not valid
            communicationService.close();
            game.processAnswer(new MyAnswer(Constants.PLAYER_DISCONNECTED, -1), this);
        }
    }

    /**
     * Processes the answer of the player.
     *
     * @param game the game of the player
     * @param myAnswer the answer of the player
     */
    private void processAnswer(Game game, MyAnswer myAnswer) {
        message = "";
        communicationService.confirmMessageReceive();
        game.processAnswer(myAnswer, this);
    }

    /**
     * Validates the answer of the player.
     *
     * @param myAnswer the answer of the player
     */
    private void validateAnswer(MyAnswer myAnswer) throws JsonParseException {
        int answerId = myAnswer.getAnswerID();
        if (!Constants.playerPasses(answerId) && (answerId < 0 || answerId > 9)) {
            throw new JsonParseException("Invalid answerID value");
        }
    }

    /**
     * Increases the no activity count of the player.
     */
    public void increaseNoActivity() {
        noActivity++;
    }

    /**
     * Resets the no activity count of the player.
     */
    public void resetNoActivity() {
        noActivity = 0;
    }

    /**
     * Returns the no activity count of the player.
     *
     * @return the no activity count of the player
     */
    public int getNoActivity() {
        return noActivity;
    }

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Resets the score of the player.
     */
    public void resetScore() {
        roundScore = 0;
    }

    /**
     * Returns the game score of the player.
     *
     * @return the game score of the player
     */
    public int getGameScore() {
        return gameScore;
    }

    /**
     * Increases the score of the player.
     */
    public void increaseScore() {
        ++roundScore;
    }

    /**
     * Sends a message to the player.
     *
     * @param json the message to be sent
     */
    public void sendMessage(JSON json) {
        communicationService.sendMessage(GsonParser.toJson(json));
    }

    /**
     * Expects an answer message from the player.
     *
     * @param timeout the timeout for the message
     */
    public void expectAnswerMessage(long timeout) {
        communicationService.expectMessage(timeout);
    }

    /**
     * Evaluates the round score of the player.
     */
    public void evaluateRoundScore() {
        gameScore += roundScore;
        roundScore = 0;
    }

    /**
     * Returns the round score of the player.
     *
     * @return the round score of the player
     */
    public int getRoundScore() {
        return roundScore;
    }
}