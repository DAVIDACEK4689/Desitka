package cz.cuni.mff.desitka.server.gameLogic;

import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.JSON.Question;
import cz.cuni.mff.desitka.JSON.client.MyAnswer;
import cz.cuni.mff.desitka.JSON.server.Answer;
import cz.cuni.mff.desitka.JSON.server.Evaluation;
import cz.cuni.mff.desitka.JSON.server.Start;
import cz.cuni.mff.desitka.JSON.server.Waiting;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;
import cz.cuni.mff.desitka.server.questions.QuestionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This abstract class represents a game in the server.
 */
public abstract class Game {
    private final int MAX_SCORE = 20;
    private final String QUESTION_DIRECTORY = "questions";
    private final int PLAYER_COUNT;
    private final long creationTime;
    private final List<Player> players = new ArrayList<>();
    private final List<Question> questions;

    private int roundNumber;
    private Round round;
    private Question question;

    /**
     * Constructs a new game with a specified player count.
     *
     * @param playerCount the number of players in the game
     */
    public Game(int playerCount) {
        this.PLAYER_COUNT = playerCount;
        creationTime = System.currentTimeMillis();
        questions = QuestionParser.getQuestions(QUESTION_DIRECTORY);
    }

    /**
     * Chooses a question for the game.
     *
     * @return the chosen question
     */
    private Question chooseQuestion() {
        if (questions.isEmpty()) {
            questions.addAll(QuestionParser.getQuestions(QUESTION_DIRECTORY));
        }
        int randomIndex = (int) (Math.random() * questions.size());
        return questions.get(randomIndex);
    }

    /**
     * Adds a player to the waiting list of the game.
     *
     * @param player the player to be added
     */
    public void addWaitingPlayer(Player player) {
        players.add(player);
        List<Waiting.WaitingPlayer> waitingPlayers = players.stream()
                .map(p -> new Waiting.WaitingPlayer(p.getName()))
                .collect(Collectors.toList());

        Waiting waiting = new Waiting(readyToStart(), waitingPlayers);
        players.forEach(p -> p.sendMessage(waiting));
    }

    /**
     * Checks if the game is ready to start.
     *
     * @return true if the game is ready to start, false otherwise
     */
    public boolean readyToStart() {
        return players.size() == PLAYER_COUNT;
    }

    /**
     * Checks if a player with a specified name has already joined the game.
     *
     * @param playerName the name of the player
     * @return true if the player has already joined, false otherwise
     */
    public boolean nameAlreadyJoined(String playerName) {
        return players.stream().anyMatch(player -> player.getName().equals(playerName));
    }

    /**
     * Returns the creation time of the game.
     *
     * @return the creation time of the game
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Returns the player count of the game.
     *
     * @return the player count of the game
     */
    public int PLAYER_COUNT() {
        return PLAYER_COUNT;
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean gameEnd() {
        return players.stream().anyMatch(player -> player.getGameScore() >= MAX_SCORE) || players.size() <= 1;
    }

    /**
     * Starts a new round of the game.
     */
    public void startRound() {
        players.forEach(Player::resetScore);
        question = chooseQuestion();
        round = new Round(++roundNumber, players, question, this);
        round.start();
    }

    /**
     * Processes the answer of a player.
     *
     * @param answer the answer of the player
     * @param player the player who answered
     */
    public void processAnswer(MyAnswer answer, Player player) {
        round.processAnswer(answer, player);
    }

    /**
     * Evaluates the round of the game.
     */
    public void evaluateRound() {
        players.forEach(Player::evaluateRoundScore);
        Evaluation evaluation = new Evaluation(question, getPlayerScore(), gameEnd());
        players.forEach(player -> player.sendMessage(evaluation));
        checkGameEnd();
    }

    /**
     * Checks if the game has ended and starts a new round if not.
     */
    private void checkGameEnd() {
        if (!gameEnd()) {
            startRound();
            return;
        }
        dealGameEnd(players.size());
    }

    /**
     * Returns the scores of the players.
     *
     * @return an array of player scores
     */
    private Evaluation.PlayerScore[] getPlayerScore() {
        return players.stream()
                .sorted((p1, p2) -> p2.getGameScore() - p1.getGameScore())
                .map(player -> new Evaluation.PlayerScore(player.getName(), player.getGameScore()))
                .toArray(Evaluation.PlayerScore[]::new);
    }

    /**
     * Checks the activity of a player.
     *
     * @param answerID the ID of the answer
     * @param player the player to check
     */
    public void checkNoActivity(int answerID, Player player) {
        player.increaseNoActivity();
        if (player.getNoActivity() > 3 || Constants.playerDisconnected(answerID)) {
            players.remove(player);
        }
    }

    /**
     * Deals with the end of the game.
     *
     * @param playerCount the number of players in the game
     */
    public void dealGameEnd(int playerCount) {}

    /**
     * Returns the game code of the game.
     *
     * @return the game code of the game
     */
    public String getGameCode() {
        return null;
    }

    /**
     * Sends an answer to the players.
     *
     * @param answer the answer to be sent
     */
    public void sendAnswer(Answer answer) {
        Turn turn = answer.getTurn();
        players.forEach(player -> {
            updateTurnData(turn, player);
            player.sendMessage(answer);
        });
    }

    /**
     * Updates the turn data of a player.
     *
     * @param turn the turn to be updated
     * @param player the player whose turn data is to be updated
     */
    private void updateTurnData(Turn turn, Player player) {
        String playerName = player.getName();
        String roundPlayer = turn.getRoundPlayer();

        turn.setMyTurn(playerName.equals(roundPlayer));
        turn.setMyScore(player.getRoundScore());
    }

    /**
     * Returns the number of players in the game.
     *
     * @return the number of players in the game
     */
    public int getGamePlayers() {
        return players.size();
    }

    /**
     * Sends the start of the game to the players.
     *
     * @param start the start of the game
     */
    public void sendStart(Start start) {
        Turn turn = start.getTurn();
        players.forEach(player -> {
            updateTurnData(turn, player);
            player.sendMessage(start);
        });
    }
}