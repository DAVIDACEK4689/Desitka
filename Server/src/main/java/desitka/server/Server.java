package desitka.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class for Desitka game.
 */
public class Server {
    private ServerSocket serverSocket;
    private final List<SinglePlayerGame> singlePlayerGames = new ArrayList<>();
    private final List<FriendGame> friendGames = new ArrayList<>();

    // WAITING TIME FOR PLAYER ANSWER - 60 seconds
    private final long WAITING_TIME = 60_000;

    // IF THERE WAS GAME EVALUATION, PLAYER NEEDS MORE TIME TO ANSWER - 35 seconds
    private final long EXTRA_TIME = 30_000;

    /**
     * Starts the server on the specified port and loads questions from the specified file.
     *
     * @param port the port on which to start the server
     * @param file the file containing questions
     * @throws IOException if an I/O error occurs when opening the socket or loading questions
     */
    public void start(int port, String file) throws IOException {
        Game.loadQuestions(file);
        serverSocket = new ServerSocket(port);
        singlePlayerGames.add(new SinglePlayerGame());

        while (true) {
            new Player(serverSocket.accept()).start();
        }
    }

    /**
     * Inner class representing a player on the server.
     */
    class Player extends Thread {
        private final Socket clientSocket;
        private long extraTime = 0;

        private String playerName;
        private int score = 0;
        private int roundScore = 0;

        private int noResponseTime = 0;
        private Game game;
        private PrintWriter printer;
        private BufferedReader reader;

        private int myAnswerID;
        private String myAnswer;


        /**
         * Sends a message to the client.
         *
         * @param message the message to send
         */
        public void print(String message) {
            printer.println(message);
            printer.flush();
        }

        /**
         * Returns the player name.
         *
         * @return the player name
         */
        public String getPlayerName() {
            return playerName;
        }


        /**
         * Returns the player's score.
         *
         * @return the player's score
         */
        public int getScore() {
            return score;
        }


        /**
         * Constructor.
         *
         * @param socket the socket associated with the player
         */
        public Player(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * Runs the player thread.
         */
        public void run() {
            try {
                printer = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println(clientSocket);

                // SEARCHING FOR GAME
                playerName = readLine();
                String gameType = readLine();
                boolean gameFound = searchGame(gameType);

                // WAITING FOR OTHER PLAYERS
                if (!gameFound) {
                    reader.close();
                    printer.close();
                    clientSocket.close();
                    return;
                }
                waitForPlayers();

                // PLAYING
                System.out.println("Game started");
                playGame();

                // GAME FINISHED
                System.out.println("Game finished");

                // ADD FRIEND GAME TO BE PLAYED AGAIN IF THERE IS MORE THAN ONE PLAYER
                int players = game.getPlayersCount();
                if (game instanceof FriendGame && players > 1) {
                    String gameCode = ((FriendGame) game).getGameCode();
                    friendGames.add(new FriendGame(players, gameCode));
                }

                // CLOSE CONNECTION
                reader.close();
                printer.close();
                clientSocket.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        /**
         * Plays the game, which consists of rounds.
         *
         * @throws IOException if an I/O error occurs
         */
        private void playGame() throws IOException {
            while (!game.gameFinished()) {
                startRound();
                playRound();
                evaluateRound();
            }
        }

        /**
         * Plays a round of the game.
         *
         * @throws IOException if an I/O error occurs
         */
        private void playRound() throws IOException {
            while (!game.roundFinished()) {
                if (game.myTurn(playerName)) {
                    makeTurn();
                }
                else {
                    Thread.yield();
                }
            }
        }

        /**
         * Makes a turn in the game. Only one player can make a turn at a time.
         *
         * @throws IOException if an I/O error occurs when reading from the input stream
         */
        private synchronized void makeTurn() throws IOException {
            // SEND PLAYER ON MOVE NAME
            game.getPlayers().forEach(player -> player.print(playerName));

            // LOAD MY ANSWER
            loadMyAnswer();

            // SEND MY ANSWER TO OTHER PLAYERS
            game.getPlayers().forEach(player -> player.print(String.valueOf(myAnswerID)));
            game.getPlayers().forEach(player -> player.print(myAnswer));

            // EVALUATE MY ANSWER
            game.evaluateAnswer(this, myAnswerID, myAnswer);

            // SCHEDULE NEXT PLAYER
            game.scheduleNextPlayer();
        }

        /**
         * Load Player Answer. If player does not respond in 40 seconds, We expect that he lost connection
         * and is removed from game.
         *
         * @throws IOException if an I/O error occurs when reading from the input stream
         */
        private void loadMyAnswer() throws IOException {
            long startTime = System.currentTimeMillis();
            while (!reader.ready()) {
                if (System.currentTimeMillis() - startTime > WAITING_TIME + extraTime) {
                    extraTime = 0;
                    myAnswerID = -1;
                    myAnswer = "noActivity";
                    System.out.println("Player " + playerName + " lost connection");

                    // PLAYER LOST CONNECTION (3 INACTIVITIES = PLAYER WILL BE REMOVED FROM GAME)
                    noResponseTime = 3;
                    return;
                }
            }
            extraTime = 0;
            myAnswerID = Integer.parseInt(readLine());
            myAnswer = readLine();
        }

        /**
         * Starts a round of the game.
         */
        private void startRound() {
            roundScore = 0;
            game.startRound();

            while (!game.roundStarted()) {
                Thread.yield();
            }
        }

        /**
         * Evaluates a round of the game.
         */
        private void evaluateRound() {
            score += roundScore;
            game.evaluateRound();

            while (!game.roundEvaluated()) {
                Thread.yield();
            }

            // PROVIDE EXTRA TIME FOR ANSWER
            extraTime = EXTRA_TIME;
        }

        /**
         * Waits for other players to join the game.
         */
        private void waitForPlayers() {
            // NOTIFY OTHER PLAYERS
            game.notifyPlayerJoined(this);

            // NOTIFY ME
            print(Long.toString(game.getCreationTime()));
            game.getPlayers().forEach(player -> print(player.getPlayerName()));

            while (!game.ready()) {
                Thread.yield();
            }
        }

        /**
         * Reads a line from the input stream.
         *
         * @return the read line
         * @throws IOException if an I/O error occurs when reading from the input stream
         */
        private String readLine() throws IOException {
            while (!reader.ready()) {
                Thread.yield();
            }
            return reader.readLine();
        }

        /**
         * Searches for a game to join.
         *
         * @param gameType the type of game to search for
         * @return true if a game was found, false otherwise
         * @throws IOException if an I/O error occurs when reading from the input stream
         */
        private synchronized boolean searchGame(String gameType) throws IOException {
            // SEARCHING FOR SINGLE PLAYER GAME
            if ("singleplayer".equals(gameType)) {
                findSinglePlayerGame(this);
            }

            // CREATING FRIEND GAME
            else if ("createGame".equals(gameType)) {
                int playerCount = Integer.parseInt(readLine());
                createFriendGame(this, playerCount);
                print(((FriendGame) game).getGameCode());
            }

            // JOINING FRIEND GAME
            else if ("joinGame".equals(gameType)) {
                try {
                    String gameCode = readLine();
                    joinFriendGame(this, gameCode);

                    print("joined");
                    print(Integer.toString(game.getPlayersCount()));
                }
                catch (NonExistingGame e) {
                    print("nonExistingGame");
                    return false;
                }
                catch (NameAlreadyJoined e) {
                    print("nameAlreadyJoined");
                    return false;
                }
            }
            return true;
        }

        /**
         * adds a score to the player's round score.
         */
        public void addScore() {
            roundScore += 1;
        }

        /**
         * resets the player's round score.
         */
        public void resetScore() {
            roundScore = 0;
        }

        /**
         * increase NoResponseTime of the player. If noResponseTime reaches 3, the player is removed from the game.
         */
        public void addNoResponseTime() {
            if (noResponseTime < 3) {
                noResponseTime++;
            }
        }

        /**
         * Get player noResponseTime.
         * @return noResponseTime
         */
        public int getNoResponseTime() {
            return noResponseTime;
        }

        /**
         * resets the player's noResponseTime.
         */
        public void resetNoResponseTime() {
            noResponseTime = 0;
        }
    }

    /**
     * Searches for a single player game to join.
     * @param player the player to join the game
     */
    private void findSinglePlayerGame(Player player) {
        removeOldSinglePlayerGames();
        if (!suitableSinglePlayerGameFound(player)) {
            createSinglePlayerGame(player);
        }
    }

    /**
     * Removes single player games that are older than 60 seconds.
     */
    private void removeOldSinglePlayerGames() {
        int index = 0;
        long currentTime = System.currentTimeMillis();

        for (SinglePlayerGame game : singlePlayerGames) {
            if (currentTime - game.getCreationTime() > 60_000) {
                index++;
            }
            else {
                break;
            }
        }
        singlePlayerGames.subList(0, index).clear();
    }


    /**
     * try to find a single player game in which the player name will be unique.
     *
     * @param player the player to join the game
     * @return true if a game was found, false otherwise
     */
    private boolean suitableSinglePlayerGameFound(Player player) {
        for (SinglePlayerGame game : singlePlayerGames) {
            if (!game.NameAlreadyJoined(player.getPlayerName())) {
                addPlayerToGame(player, game);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a player to a game.
     *
     * @param player the player to add
     * @param game the game to add the player to
     */
    private void addPlayerToGame(Player player, Game game) {
        game.addPlayer(player);
        player.game = game;

        if (game.ready()) {
            removeGame(game);
        }
    }

    /**
     * Removes a game from the list of accessible games.
     *
     * @param game the game to remove
     */
    private void removeGame(Game game) {
        if (game instanceof SinglePlayerGame) {
            singlePlayerGames.remove(game);
        }
        else if (game instanceof FriendGame) {
            friendGames.remove(game);
        }
    }

    /**
     * Create a single player game.
     *
     * @param player the player to join the game
     */
    private void createSinglePlayerGame(Player player) {
        SinglePlayerGame newGame = new SinglePlayerGame();
        player.game = newGame;
        newGame.addPlayer(player);
        singlePlayerGames.add(newGame);
    }

    /**
     * Searches for a friend game to join.
     *
     * @param player the player to join the game
     * @param code the code of the game to join
     * @throws NonExistingGame if the game does not exist
     */
    private void joinFriendGame(Player player, String code) throws NonExistingGame, NameAlreadyJoined {
        removeOldFriendGames();
        for (FriendGame game : friendGames) {
            if (game.getGameCode().equals(code)) {
                checkPlayerName(player, game);
                addPlayerToGame(player, game);
                return;
            }
        }
        throw new NonExistingGame("Game with code " + code + " does not exist");
    }

    /**
     * Removes friend games that are older than 60 seconds.
     */
    private void removeOldFriendGames() {
        int index = 0;
        long currentTime = System.currentTimeMillis();

        for (FriendGame game : friendGames) {
            if (currentTime - game.getCreationTime() > 60_000) {
                index++;
            }
            else {
                break;
            }
        }
        friendGames.subList(0, index).clear();
    }

    /**
     * Checks if the player's name is already joined in the game.
     *
     * @param player the player to join the game
     * @param game the game to join
     * @throws NameAlreadyJoined if the player's name is already joined in the game
     */
    private void checkPlayerName(Player player, FriendGame game) throws NameAlreadyJoined {
        if (game.NameAlreadyJoined(player.getPlayerName())) {
            throw new NameAlreadyJoined("Name already joined");
        }
    }

    /**
     * Creates a friend game.
     * @param player the player creating the game
     * @param playerCount the number of players in the game
     */
    private void createFriendGame(Player player, int playerCount) {
        FriendGame friendGame = new FriendGame(playerCount);
        player.game = friendGame;
        friendGame.addPlayer(player);
        friendGames.add(friendGame);
    }

    /**
     * Exception thrown when a searched FriendGame does not exist.
     */
    private static class NonExistingGame extends Exception {
        public NonExistingGame(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * Exception thrown when a player tries to join a game with a name that is already taken.
     */
    private static class NameAlreadyJoined extends Exception {
        public NameAlreadyJoined(String errorMessage) {
            super(errorMessage);
        }
    }
}
