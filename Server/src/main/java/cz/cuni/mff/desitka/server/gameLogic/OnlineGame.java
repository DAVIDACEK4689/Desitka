package cz.cuni.mff.desitka.server.gameLogic;

/**
 * This class represents an online game in the server.
 */
public class OnlineGame extends Game {
    private final static int PLAYER_COUNT = 2;

    /**
     * Constructs a new online game with a fixed player count.
     */
    public OnlineGame() {
        super(PLAYER_COUNT);
    }
}