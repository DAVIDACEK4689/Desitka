package cz.cuni.mff.desitka.logic.game;

import java.util.HashMap;

import static cz.cuni.mff.desitka.interfaces.GameStates.GameState.*;

import cz.cuni.mff.desitka.interfaces.GameStates;

/**
 * Constants for game logic.
 * This class provides a mapping between game states and their corresponding scene times.
 */
public class GameConstants implements GameStates {
    private final static HashMap<GameState, Long> timeMap = new HashMap<GameState, Long>() {
        {
            put(GAME_START, 0L);
            put(ROUND_START, 3_000L);
            put(ROUND_PLAYER, 3_000L);
            put(SHOW_ANSWER, 3_000L);
            put(SHOW_ANSWERS, 10_000L);
            put(ROUND_END, 5_000L);

            // infinite time for the following states
            put(PLAYER_ANSWERS, Long.MAX_VALUE);
            put(PLAYER_ANSWERS_DETAIL, Long.MAX_VALUE);
            put(GAME_END, Long.MAX_VALUE);
        }
    };

    /**
     * Returns the scene time for a given game state.
     * @param gameState The game state.
     * @return The scene time for the given game state.
     */
    public static long getSceneTime(GameState gameState) {
        return timeMap.get(gameState);
    }
}