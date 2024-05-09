package cz.cuni.mff.desitka.JSON.client;

import cz.cuni.mff.desitka.JSON.JSON;

/**
 * The MyJoining class represents a player's joining request in the game.
 */
public class MyJoining extends JSON implements MyJoiningRequests {
    private final RequestType requestType;
    private final String playerName;
    private final String gameCode;
    private final int playerCount;

    /**
     * Constructs a new MyJoining object.
     *
     * @param requestType The type of the request.
     * @param playerName The name of the player.
     * @param gameCode The code of the game.
     * @param playerCount The count of the players.
     */
    public MyJoining(RequestType requestType, String playerName, String gameCode, int playerCount) {
        this.requestType = requestType;
        this.playerName = playerName;
        this.gameCode = gameCode;
        this.playerCount = playerCount;
    }

    /**
     * Returns the type of the request.
     *
     * @return The type of the request.
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Returns the name of the player.
     *
     * @return The name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the code of the game.
     *
     * @return The code of the game.
     */
    public String getGameCode() {
        return gameCode;
    }

    /**
     * Returns the count of the players.
     *
     * @return The count of the players.
     */
    public int getPlayerCount() {
        return playerCount;
    }
}