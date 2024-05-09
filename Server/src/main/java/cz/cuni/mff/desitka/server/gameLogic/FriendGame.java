package cz.cuni.mff.desitka.server.gameLogic;

import cz.cuni.mff.desitka.JSON.client.MyJoining;
import cz.cuni.mff.desitka.server.GameManager;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * This class represents a friend game in the server.
 */
public class FriendGame extends Game {
    private final GameManager gameManager;
    private final String gameCode;

    /**
     * Constructs a new friend game with a generated game code.
     *
     * @param playerCount the number of players in the game
     * @param gameManager the game manager of the server
     */
    public FriendGame(int playerCount, GameManager gameManager) {
        super(playerCount);
        this.gameManager = gameManager;
        gameCode = generateGameCode();
    }

    /**
     * Constructs a new friend game with a specific game code.
     *
     * @param playerCount the number of players in the game
     * @param gameManager the game manager of the server
     * @param gameCode the code of the game
     */
    public FriendGame(int playerCount, GameManager gameManager, String gameCode) {
        super(playerCount);
        this.gameManager = gameManager;
        this.gameCode = gameCode;
    }

    /**
     * Returns the game code of this friend game.
     *
     * @return the game code of this friend game
     */
    @Override
    public String getGameCode() {
        return gameCode;
    }

    /**
     * Deals with the end of the game.
     *
     * @param playerCount the number of players in the game
     */
    @Override
    public void dealGameEnd(int playerCount) {
        if (playerCount > 1) {
            MyJoining myJoining = new MyJoining(null, null, gameCode, playerCount);
            gameManager.connectPlayer(myJoining, null);
        }
    }

    /**
     * Generates a new game code.
     *
     * @return the generated game code
     */
    private String generateGameCode() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }
}