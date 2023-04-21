package desitka.server;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract class that represents a game.
 */
abstract class Game {
    private static final int MAX_SCORE = 1;
    private static List<Question> questions;
    private final long creationTime;

    private final List<Server.Player> players = new ArrayList<>();
    private final List<Server.Player> playersInRound = new ArrayList<>();
    private final List<Integer> questionIDs = new ArrayList<>();
    private final Random random = new Random();

    private volatile int PLAYER_COUNT;
    private volatile int onMoveIndex;
    private volatile String playerOnMove;

    private volatile boolean roundStarted;
    private volatile boolean roundEvaluated;

    private Question question;
    private int ROUND = 0;
    private int playerMaxScore = 0;
    private int roundEvaluateReadyPlayers = 0;
    private int roundStartReadyPlayers = 0;


    /**
     * Returns the creation time of the game.
     *
     * @return the creation time of the game
     */
    public long getCreationTime() {
        return creationTime;
    }


    /**
     * Returns the list of players in the game.
     *
     * @return the list of players in the game
     */
    public List<Server.Player> getPlayers() {
        return players;
    }


    /**
     * Constructor.
     *
     * @param playerCount the number of players in the game
     */
    Game(int playerCount) {
        creationTime = System.currentTimeMillis();
        PLAYER_COUNT = playerCount;
        roundStarted = false;
        loadQuestionIDs();
    }


    /**
     * Loads the list of question IDs.
     */
    private void loadQuestionIDs() {
        for (int i = 0; i < questions.size(); i++) {
            questionIDs.add(i);
        }
    }


    /**
     * Loads the list of questions from the specified file.
     *
     * @param file the name of the file containing the questions
     * @throws IOException if there is an error reading the file
     */
    public static void loadQuestions(String file) throws IOException {
        questions = QuestionParser.getQuestions(file);
    }


    /**
     * Adds a player to the game.
     *
     * @param player the player to add
     */
    protected void addPlayer(Server.Player player) {
        players.add(player);
    }


    /**
     * Removes a player from the game.
     *
     * @param player the player to remove
     */
    private void removePlayer(Server.Player player) {
        players.remove(player);
        PLAYER_COUNT--;
    }


    /**
     * Check player inactivity. If the player has not responded for 3 times, remove him from the game.
     *
     * @param player the player to check
     */
    private void checkPlayerInactivity(Server.Player player) {
        if (player.getNoResponseTime() == 3) {
            removePlayer(player);
        }
    }


    /**
     * Decides whether the answer is correct.
     *
     * @param questionID the ID of the question
     * @param answer the answer of a player
     * @return true if the answer is correct, false otherwise
     */
    private boolean isAnswerCorrect(int questionID, String answer) {
        return question.isAnswerCorrect(questionID, answer);
    }


    /**
     * Get the number of players in the game.
     * @return the number of players in the game
     */
    public int getPlayersCount () {
        return PLAYER_COUNT;
    }


    /**
     * Post the question to all players.
     */
    private void postQuestion() {
        // PROVIDE SUB-QUESTIONS AND ANSWERS
        players.forEach(player -> {
            player.print(question.getText());
            question.getSubQuestions().forEach(subQuestion -> {
                player.print(subQuestion.getText());
                player.print(subQuestion.getAnswer());
            });
        });
    }


    /**
     * Randomly choose next question.
     */
    private void chooseQuestion() {
        int randomIndex = random.nextInt(questionIDs.size()-1);
        question = questions.remove(randomIndex);
    }


    /**
     * Notify other players that a player has joined the game.
     * @param player the player that has joined the game
     */
    protected void notifyPlayerJoined(Server.Player player) {
        for (Server.Player p : players) {
            if (p != player) {
                p.print(player.getPlayerName());
            }
        }
    }


    /**
     * Decides whether the game is ready to start. Game is ready to start if all players have joined.
     * @return true if all players has joined, false otherwise
     */
    public boolean ready() {
        return players.size() == PLAYER_COUNT;
    }


    /**
     * Decides whether the round is finished. Round is finished if there is no active player in round.
     * @return true if there is no active player in round, false otherwise
     */
    public boolean roundFinished() {
        return playersInRound.size() == 0;
    }


    /**
     * Start next round. All players are synchronized at this point.
     */
    public synchronized void startRound() {
        roundStartReadyPlayers++;

        if (roundStartReadyPlayers == PLAYER_COUNT) {
            // UPDATE PLAYERS IN ROUND
            playersInRound.clear();
            playersInRound.addAll(players);

            // GET PLAYER ON MOVE
            onMoveIndex = ROUND % PLAYER_COUNT;
            playerOnMove = playersInRound.get(onMoveIndex).getPlayerName();

            // CHOOSE QUESTION
            ROUND++;
            chooseQuestion();
            postQuestion();

            // UPDATE VALUES
            roundStartReadyPlayers = 0;
            roundEvaluated = false;
            roundStarted = true;
        }
    }


    /**
     * Evaluate the round. All players are synchronized at this point.
     */
    public synchronized void evaluateRound() {
        roundEvaluateReadyPlayers++;

        if (roundEvaluateReadyPlayers == PLAYER_COUNT) {
            // FIND MAX SCORE
            List<Server.Player> playerScoreList = new ArrayList<>(players);
            playerScoreList.sort((p1, p2) -> p2.getScore() - p1.getScore());
            playerMaxScore = playerScoreList.get(0).getScore();

            // NOTIFY PLAYERS
            players.forEach(player -> {
                player.print(String.valueOf(players.size()));
                player.print(String.valueOf(gameFinished()));
                playerScoreList.forEach(p -> {
                    player.print(p.getPlayerName());
                    player.print(String.valueOf(p.getScore()));
                });
            });

            // UPDATE VALUES
            roundEvaluateReadyPlayers = 0;
            roundStarted = false;
            roundEvaluated = true;
        }
    }


    /**
     * Decides whether the game is finished. Game is finished if the player with the highest score
     * has reached the maximum score or there is only one player left.
     *
     * @return true if the game is finished, false otherwise
     */
    public boolean gameFinished() {
        return playerMaxScore >= MAX_SCORE || PLAYER_COUNT <= 1;
    }


    /**
     * Schedule the next player to play.
     */
    public void scheduleNextPlayer() {
        if (roundFinished()) {
            endRound();
            return;
        }

        // update onMoveIndex
        onMoveIndex = onMoveIndex % playersInRound.size();
        playerOnMove = playersInRound.get(onMoveIndex).getPlayerName();
    }


    /**
     * End the round.
     */
    private void endRound() {
        players.forEach(player -> player.print("roundFinished"));
    }


    /**
     * Method to check whether it is the player's turn.
     *
     * @param playerName the name of the player
     * @return true if it is the player's turn, false otherwise
     */
    public boolean myTurn(String playerName) {
        return playerName.equals(playerOnMove);
    }


    /**
     * Evaluate the answer of a player.
     *
     * @param player the player that has answered
     * @param myAnswerID the ID of the answer
     * @param myAnswer the answer of the player
     */
    public void evaluateAnswer(Server.Player player, int myAnswerID, String myAnswer) {
        if ("noActivity".equals(myAnswer)) {
                player.addNoResponseTime();
                playersInRound.remove(player);
                checkPlayerInactivity(player);
        }
        else if ("pass".equals(myAnswer)) {
                player.resetNoResponseTime();
                playersInRound.remove(player);
        }
        else {
            player.resetNoResponseTime();
            checkAnswerCorrectness(player, myAnswerID, myAnswer);
        }
    }


    /**
     * Check answer correctness.
     *
     * @param player the player that has answered
     * @param myAnswerID the ID of the answer
     * @param myAnswer the answer of the player
     */
    private void checkAnswerCorrectness(Server.Player player, int myAnswerID, String myAnswer) {
        if (isAnswerCorrect(myAnswerID, myAnswer)) {
            player.addScore();
            onMoveIndex++;
        }
        else {
            player.resetScore();
            playersInRound.remove(player);
        }
    }


    /**
     * Method to check whether the round has started. Important for synchronization.
     *
     * @return true if the round has started, false otherwise
     */
    public boolean roundStarted() {
        return roundStarted;
    }


    /**
     * Method to check whether the round has been evaluated. Important for synchronization.
     *
     * @return true if the round has been evaluated, false otherwise
     */
    public boolean roundEvaluated() {
        return roundEvaluated;
    }


    /**
     * Method to check whether a player with given name has already joined the game.
     *
     * @param playerName the name of the player
     * @return true if the player has already joined, false otherwise
     */
    public boolean NameAlreadyJoined(String playerName) {
        for (Server.Player player : players) {
            if (player.getPlayerName().equals(playerName)) {
                return true;
            }
        }
        return false;
    }
}


/**
 * Class to represent a SinglePlayerGame.
 * This Game has default number of players = 3 and no code.
 */
class SinglePlayerGame extends Game {
    private static final int SINGLE_PLAYER_COUNT = 3;

    /**
     * Constructor for SinglePlayerGame.
     */
    SinglePlayerGame() {
        super(SINGLE_PLAYER_COUNT);
    }

}


/**
 * Class to represent a FriendGame.
 * This Game has a code and number of players is 2-5.
 */
class FriendGame extends Game {
    private final String gameCode;

    /**
     * Constructor for FriendGame.
     *
     * @param playerCount the number of players
     */
    FriendGame(int playerCount) {
        super(playerCount);
        gameCode = generateGameCode();
    }


    /**
     * Constructor for FriendGame which will be played again. The game code is the same as before
     *
     * @param playerCount the number of players
     */
    FriendGame(int playerCount, String gameCode) {
        super(playerCount);
        this.gameCode = gameCode;
    }


    /**
     * Generate a random game code.
     *
     * @return the game code
     */
    private String generateGameCode() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }


    /**
     * Get the game code.
     *
     * @return the game code
     */
    public String getGameCode() {
        return gameCode;
    }
}