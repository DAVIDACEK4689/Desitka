package cz.cuni.mff.desitka.interfaces;

/**
 * Represents the game states in the game.
 * This interface holds the enumeration of possible game states.
 */
public interface GameStates {

    /**
     * Enumeration of possible game states.
     */
    enum GameState {
        /**
         * Represents the game start state.
         */
        GAME_START,

        /**
         * Represents the round start state.
         */
        ROUND_START,

        /**
         * Represents the round player state.
         */
        ROUND_PLAYER,

        /**
         * Represents the player answers state.
         */
        PLAYER_ANSWERS,

        /**
         * Represents the player answers detail state.
         */
        PLAYER_ANSWERS_DETAIL,

        /**
         * Represents the show answer state.
         */
        SHOW_ANSWER,

        /**
         * Represents the show answers state.
         */
        SHOW_ANSWERS,

        /**
         * Represents the round end state.
         */
        ROUND_END,

        /**
         * Represents the game end state.
         */
        GAME_END;
    }
}