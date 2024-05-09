package cz.cuni.mff.desitka.server;

import cz.cuni.mff.desitka.JSON.client.MyJoining;
import cz.cuni.mff.desitka.JSON.client.MyJoiningRequests;
import cz.cuni.mff.desitka.JSON.server.Joining;
import cz.cuni.mff.desitka.JSON.server.JoiningResults;
import cz.cuni.mff.desitka.server.gameLogic.Player;
import cz.cuni.mff.desitka.server.gameLogic.FriendGame;
import cz.cuni.mff.desitka.server.gameLogic.Game;
import cz.cuni.mff.desitka.server.gameLogic.OnlineGame;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static cz.cuni.mff.desitka.JSON.Constants.WAITING_TIME;

/**
 * This class manages the games and players in the server.
 */
public class GameManager implements MyJoiningRequests, JoiningResults {
    private final List<OnlineGame> onlineGames = new ArrayList<>();
    private final List<FriendGame> friendGames = new ArrayList<>();

    /**
     * Adds a new player to the game manager.
     *
     * @param socket the socket of the new player
     */
    public void addPlayer(Socket socket) {
        System.out.println("Player connected: " + socket);
        new Player(socket, this);
    }

    /**
     * Connects a player to a game based on their joining request.
     *
     * @param myJoining the joining request of the player
     * @param player the player to be connected
     */
    public synchronized void connectPlayer(MyJoining myJoining, Player player) {
        removeOldGames(onlineGames);
        removeOldGames(friendGames);

        if (player == null) {
            createFriendGameAgain(myJoining.getPlayerCount(), myJoining.getGameCode());
            return;
        }

        switch (myJoining.getRequestType()) {
            case JOIN_ONLINE_GAME:
                joinOnlineGame(player);
                break;
            case JOIN_FRIEND_GAME:
                joinFriendGame(player, myJoining.getGameCode());
                break;
            case CREATE_FRIEND_GAME:
                createFriendGame(player, myJoining.getPlayerCount());
                break;
        }
    }

    /**
     * Joins a player to an online game.
     *
     * @param player the player to be joined
     */
    private void joinOnlineGame(Player player) {
        for (OnlineGame game : onlineGames) {
            if (!game.nameAlreadyJoined(player.getName())) {
                player.setGame(game);
                game.addWaitingPlayer(player);

                if (game.readyToStart()) {
                    onlineGames.remove(game);
                    game.startRound();
                }
                return;
            }
        }
        createOnlineGame(player);
    }

    /**
     * Joins a player to a friend game.
     *
     * @param player the player to be joined
     * @param gameCode the code of the game to join
     */
    private void joinFriendGame(Player player, String gameCode) {
        for (FriendGame game : friendGames) {
            if (game.getGameCode().equals(gameCode)) {
                if (game.nameAlreadyJoined(player.getName())) {
                    player.sendMessage(new Joining(JoiningResult.NAME_ALREADY_JOINED, null, 0, 0));
                    return;
                }
                player.setGame(game);
                game.addWaitingPlayer(player);

                if (game.readyToStart()) {
                    friendGames.remove(game);
                    game.startRound();
                }
                return;
            }
        }
        player.sendMessage(new Joining(JoiningResult.GAME_NOT_FOUND, null, 0, 0));
    }

    /**
     * Creates a new online game and adds a player to it.
     *
     * @param player the player to be added
     */
    private void createOnlineGame(Player player) {
        OnlineGame game = new OnlineGame();
        player.setGame(game);
        game.addWaitingPlayer(player);
        onlineGames.add(game);
    }

    /**
     * Creates a new friend game and adds a player to it.
     *
     * @param player the player to be added
     * @param playerCount the number of players in the game
     */
    private void createFriendGame(Player player, int playerCount) {
        FriendGame game = new FriendGame(playerCount, this);
        player.setGame(game);
        game.addWaitingPlayer(player);
        friendGames.add(game);
    }

    /**
     * Creates a new friend game with a specific game code.
     *
     * @param playerCount the number of players in the game
     * @param gameCode the code of the game
     */
    private void createFriendGameAgain(int playerCount, String gameCode) {
        FriendGame game = new FriendGame(playerCount, this, gameCode);
        friendGames.add(game);
    }

    /**
     * Removes games that have been waiting for too long.
     *
     * @param games the list of games to check
     */
    private void removeOldGames(List<? extends Game> games) {
        int index = 0;
        long currentTime = System.currentTimeMillis();

        for (Game game : games) {
            if (currentTime - game.getCreationTime() > WAITING_TIME) {
                index++;
                continue;
            }
            break;
        }
        games.subList(0, index).clear();
    }
}