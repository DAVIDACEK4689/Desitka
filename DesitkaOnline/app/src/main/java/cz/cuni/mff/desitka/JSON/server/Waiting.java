package cz.cuni.mff.desitka.JSON.server;

import java.util.List;

import cz.cuni.mff.desitka.JSON.JSON;

/**
 * The Waiting class represents the waiting state in the game.
 */
public class Waiting extends JSON {
    private final boolean startReady;
    private final List<WaitingPlayer> waitingPlayers;

    /**
     * Constructs a new Waiting object.
     *
     * @param startReady Indicates if the game is ready to start.
     * @param waitingPlayers The list of players who are waiting.
     */
    public Waiting(boolean startReady, List<WaitingPlayer> waitingPlayers) {
        this.startReady = startReady;
        this.waitingPlayers = waitingPlayers;
    }

    /**
     * Checks if the game is ready to start.
     *
     * @return true if the game is ready to start, false otherwise.
     */
    public boolean startReady() {
        return startReady;
    }

    /**
     * Returns the list of players who are waiting.
     *
     * @return The list of players who are waiting.
     */
    public List<WaitingPlayer> getWaitingPlayers() {
        return waitingPlayers;
    }

    /**
     * The WaitingPlayer class represents a player who is waiting.
     */
    public static class WaitingPlayer {
        private final String playerName;

        /**
         * Constructs a new WaitingPlayer object.
         *
         * @param playerName The name of the player.
         */
        public WaitingPlayer(String playerName) {
            this.playerName = playerName;
        }

        /**
         * Returns the name of the player.
         *
         * @return The name of the player.
         */
        public String getPlayerName() {
            return playerName;
        }
    }
}