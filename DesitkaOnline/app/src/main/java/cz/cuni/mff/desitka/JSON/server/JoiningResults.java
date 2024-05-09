package cz.cuni.mff.desitka.JSON.server;

/**
 * The JoiningResults interface defines the possible results of a player attempting to join a game.
 */
public interface JoiningResults {
    /**
     * The JoiningResult enum represents the different results of a player attempting to join a game.
     */
    enum JoiningResult {
        /**
         * The player has successfully joined the game.
         */
        JOINED,
        
        /**
         * The game was not found.
         */
        GAME_NOT_FOUND,

        /**
         * The player's name is already in use in the game.
         */
        NAME_ALREADY_JOINED
    }
}