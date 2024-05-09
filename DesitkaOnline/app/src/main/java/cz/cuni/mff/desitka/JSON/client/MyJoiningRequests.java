package cz.cuni.mff.desitka.JSON.client;

/**
 * The MyJoiningRequests interface represents the types of requests a player can make when joining a game.
 */
public interface MyJoiningRequests {
    /**
     * The RequestType enum represents the types of requests a player can make.
     */
    enum RequestType {
        /**
         * Represents a request to join an online game.
         */
        JOIN_ONLINE_GAME,

        /**
         * Represents a request to join a friend's game.
         */
        JOIN_FRIEND_GAME,

        /**
         * Represents a request to create a new friend's game.
         */
        CREATE_FRIEND_GAME,
    }
}