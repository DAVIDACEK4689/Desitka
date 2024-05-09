package cz.cuni.mff.desitka.JSON;

/**
 * The GameStates interface defines the various states a game can be in.
 */
public interface GameStates {
    /**
     * The GameState enum represents the different states a game can have.
     */
    enum GameState {
        /**
         * The game is in the process of joining.
         */
        GAME_JOINING,

        /**
         * The game is waiting for players to join.
         */
        WAITING_FOR_PLAYERS,

        /**
         * The game has started.
         */
        GAME_START,

        /**
         * A new round has started in the game.
         */
        ROUND_START,

        /**
         * It's a player's turn in the round.
         */
        ROUND_PLAYER,

        /**
         * The player is answering a question.
         */
        PLAYER_ANSWERS,

        /**
         * The player is viewing the details of their answer.
         */
        PLAYER_ANSWERS_DETAIL,

        /**
         * The game is waiting to show the answer.
         */
        SHOW_ANSWER_WAITING,

        /**
         * The game is showing the answer.
         */
        SHOW_ANSWER,

        /**
         * The game is showing all the answers.
         */
        SHOW_ANSWERS,

        /**
         * The round has ended.
         */
        ROUND_END,

        /**
         * The game has ended.
         */
        GAME_END
    }
}