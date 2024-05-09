package cz.cuni.mff.desitka.JSON;

import static cz.cuni.mff.desitka.JSON.GameStates.GameState.*;
import java.util.HashMap;

/**
 * The Constants class provides constant values and utility methods for the game states.
 */
public class Constants implements GameStates {
    /**
     * The server URL.
     */
    private final static String SERVER_URL = "0.tcp.eu.ngrok.io";

    /**
     * The server port.
     */
    private final static int SERVER_PORT = 0;

    /**
     * The waiting time in milliseconds.
     */
    public final static long WAITING_TIME = 60_000L; // 60 seconds

    /**
     * The answer time in milliseconds.
     */
    public final static long ANSWER_TIME = 30_000L;  // 30 seconds

    /**
     * The extra time in milliseconds.
     */
    public final static long EXTRA_TIME = 1_500L;    // 1.5 seconds

    /**
     * The constant for player passes.
     */
    public final static int PLAYER_PASSES = -1;

    /**
     * The constant for player passes without activity.
     */
    public final static int PLAYER_PASSES_WITHOUT_ACTIVITY = -10;

    /**
     * The constant for player disconnected.
     */
    public final static int PLAYER_DISCONNECTED = -100;

    /**
     * The map of game states to their respective times.
     */
    private final static HashMap<GameState, Long> timeMap = new HashMap<GameState, Long>() {
        {
            put(GAME_JOINING, 2 * EXTRA_TIME);
            put(WAITING_FOR_PLAYERS, WAITING_TIME);
            put(GAME_START, 3_000L);
            put(ROUND_START, 3_000L);
            put(ROUND_PLAYER, 3_000L);
            put(PLAYER_ANSWERS, ANSWER_TIME);
            put(SHOW_ANSWER_WAITING, 2 * EXTRA_TIME);
            put(SHOW_ANSWER, 3_000L);
            put(SHOW_ANSWERS, 10_000L);
            put(ROUND_END, 5_000L);
            put(GAME_END, WAITING_TIME - 20_000);
        }
    };

    /**
     * Returns the scene time for a given game state.
     *
     * @param gameState The game state.
     * @return The scene time.
     */
    public static long getSceneTime(GameState gameState) {
        return timeMap.get(gameState);
    }

    /**
     * Checks if the player passes.
     *
     * @param answerID The answer ID.
     * @return true if the player passes, false otherwise.
     */
    public static boolean playerPasses(int answerID) {
        return answerID == PLAYER_PASSES || answerID == PLAYER_PASSES_WITHOUT_ACTIVITY;
    }

    /**
     * Checks if the player disconnected.
     *
     * @param answerID The answer ID.
     * @return true if the player disconnected, false otherwise.
     */
    public static boolean playerDisconnected(int answerID) {
        return answerID == PLAYER_DISCONNECTED;
    }

    /**
     * Checks if the player did not answer.
     *
     * @param answerID The answer ID.
     * @return true if the player did not answer, false otherwise.
     */
    public static boolean playerNoAnswer(int answerID) {
        return playerPasses(answerID) || playerDisconnected(answerID);
    }

    /**
     * Returns the server URL.
     *
     * @return The server URL.
     */
    public static String getServerURL() {
        return SERVER_URL;
    }

    /**
     * Returns the server port.
     *
     * @return The server port.
     */
    public static int getServerPort() {
        return SERVER_PORT;
    }

    /**
     * Returns the round answer time for a given round number.
     *
     * @param roundNumber The round number.
     * @return The round answer time.
     */
    public static long getRoundAnswerTime(int roundNumber) {
        if (roundNumber == 1) {
            return timeMap.get(GAME_START) + timeMap.get(ROUND_START) + timeMap.get(ROUND_PLAYER) + ANSWER_TIME;
        }
        return timeMap.get(SHOW_ANSWER) + timeMap.get(SHOW_ANSWERS) + timeMap.get(ROUND_END) +
                timeMap.get(ROUND_START) + timeMap.get(ROUND_PLAYER) + ANSWER_TIME;
    }

    /**
     * Returns the regular answer time.
     *
     * @return The regular answer time.
     */
    public static long getRegularAnswerTime() {
        return timeMap.get(SHOW_ANSWER) + timeMap.get(ROUND_PLAYER) + ANSWER_TIME;
    }
}