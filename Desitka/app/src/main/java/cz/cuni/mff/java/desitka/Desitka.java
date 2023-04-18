package cz.cuni.mff.java.desitka;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Desitka extends AppCompatActivity {
    private String playerName;
    private Thread clientThread;
    private String gameCode = "";

    private final String[] questionAnswers = new String[10];

    private volatile String playerOnMove;
    private volatile String answer;
    private volatile int answerID;
    private volatile String myAnswer;
    private volatile int myAnswerID;

    private volatile boolean playerOnMoveUpdated;
    private volatile boolean answerRecorded;
    private volatile boolean playerScoreLoaded;
    private volatile boolean readyForNextRound;
    private volatile boolean gameFinished;

    private int roundScore;
    private int roundPlayers;
    private volatile int gamePlayers;
    private volatile boolean playersFound;


    // Shared preferences to save player name
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferencesEditor;


    // starting scene
    TextView playerNameTitle;
    TextView playerCountSelected;
    TextView[] playerCounts;


    // game scene
    TextView generatedGameCode;
    TextView joinedPlayersCount;
    TextView joinedGameTimer;
    TextView[] joinedPlayers;


    // question scene
    TextView questionText;
    TextView[] questions;
    TextView[] answers;
    TextView questionTimer;

    TextView playersInRound;
    TextView playerScore;


    // detail question scene
    TextView questionDetailText;
    TextView answerQuestionTimer;


    // next player scene
    TextView nextPlayerName;


    // answer verdict scene
    TextView correctAnswer;
    TextView answerVerdict;


    // player passes scene
    TextView playerPassesName;


    // player score scene
    TextView[] playerNames;
    TextView[] playerScores;
    TextView playersScoreTimer;


    // Buttons
    Button singlePlayer;
    Button multiplayer;
    Button setUpGame;
    Button joinGame;
    Button createGame;
    Button changeName;
    Button confirmCode;
    Button returnToMenu;

    Button passQuestion;
    Button answerQuestion;
    Button returnToQuestion;
    Button playAgain;
    Button endGame;


    // ImageViews
    ImageView homeIcon;
    ImageView menuIcon;


    // EditTexts
    EditText changePlayerNameInput;
    EditText gameCodeInput;
    EditText answerInput;


    // Layouts
    RelativeLayout startingLayout;
    RelativeLayout changeNameLayout;
    RelativeLayout joinGameLayout;

    RelativeLayout startingButtons;
    RelativeLayout friendGameButtons;
    RelativeLayout createGameButtons;

    RelativeLayout gameJoiningLayout;
    RelativeLayout questionLayout;
    RelativeLayout questionDetailLayout;
    RelativeLayout nextPlayerLayout;
    RelativeLayout playersScoreLayout;
    RelativeLayout playerPassesLayout;

    RelativeLayout answerLayout;
    RelativeLayout answerVerdictLayout;

    RelativeLayout gameCodeLayout;
    LinearLayout questionScoreLayout;

    RelativeLayout startingContent;
    RelativeLayout gameContent;
    RelativeLayout navbar;

    // Alert dialog to show messages
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alert;

    // Service to get internet availability
    ConnectivityManager connectivityManager;

    // Network receiver to check internet availability
    ConnectivityReceiver connectivityReceiver;

    // Input manager to hide keyboard
    InputMethodManager inputMethodManager;


    /**
     * Class to monitor internet availability
     */
    public class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isInternetAvailable()) {
                requestInternetConnection();
            }
        }
    }

    /**
     * Method called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findContent();
        initialize();
        actions();
        loadPlayerName();
        setStartingScene();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }


    /**
     * Get player name from shared preferences
     */
    private void loadPlayerName() {
        playerName = sharedPreferences.getString("username", "");
        playerNameTitle.setText(playerName);
    }

    /**
     * Update player name in shared preferences
     *
     * @param name new player name
     */
    private void updatePlayerName(String name) {
        playerName = name;
        playerNameTitle.setText(playerName);

        preferencesEditor.putString("username", playerName);
        preferencesEditor.apply();
    }

    /**
     * Initialize shared preferences and editor
     */
    private void initializePreferences() {
        sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
    }

    /**
     * Find all the views in the layout and inflate them
     */
    private void findContent() {
        startingContent = findViewById(R.id.starting_content);
        gameContent = findViewById(R.id.game_content);

        getLayoutInflater().inflate(R.layout.starting_layout, startingContent);
        getLayoutInflater().inflate(R.layout.game_layout, gameContent);
    }

    /**
     * Set the UI for the next player scene
     */
    private void setNextPlayerScene() {
        setNextPlayerUI();
        setNextPlayerLayout();
        new Handler().postDelayed(this::answerQuestionScene, 3000);
    }


    /**
     * Set the UI for the answer question scene
     */
    private void answerQuestionScene() {
        setQuestionScene();
        setQuestionTimer();
    }


    /**
     * Set the UI for the next player layout
     */
    private void setNextPlayerLayout() {
        playersScoreLayout.setVisibility(RelativeLayout.GONE);
        questionDetailLayout.setVisibility(RelativeLayout.GONE);
        playerPassesLayout.setVisibility(RelativeLayout.GONE);
        gameJoiningLayout.setVisibility(RelativeLayout.GONE);
        questionLayout.setVisibility(RelativeLayout.GONE);
        nextPlayerLayout.setVisibility(RelativeLayout.VISIBLE);
    }

    /**
     * Set the UI for the next player
     */
    private void setNextPlayerUI() {
        if (myTurn()) {
            playerPlaysUI();
            return;
        }
        playerWaitsUI();
    }


    /**
     * Set the UI for the waiting player
     */
    private void playerWaitsUI() {
        nextPlayerName.setText(String.format(getApplicationContext().getResources().getString(R.string.player_on_move), playerOnMove));
        Arrays.stream(answers).forEach(answer -> answer.setEnabled(false));
        passQuestion.setEnabled(false);
        answerQuestion.setEnabled(false);
    }

    /**
     * Set the UI for the player that plays
     */
    private void playerPlaysUI() {
        nextPlayerName.setText(R.string.your_turn);
        for (TextView answer : answers) {
            if (answer.getText().equals("?")) {
                answer.setEnabled(true);
            }
        }
        passQuestion.setEnabled(true);
        answerQuestion.setEnabled(true);
    }


    /**
     * Set the UI for the question scene
     */
    private void setQuestionTimer() {
        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                questionTimer.setText(String.valueOf((int)(millisUntilFinished / 1000)));
                answerQuestionTimer.setText(String.valueOf((int)(millisUntilFinished / 1000)));

                if (answer != null) {
                    this.cancel();
                    checkAnswer();
                }
            }

            @Override
            public void onFinish() {
                if (myTurn()) {
                    myAnswer = "noActivity";
                }

                // waiting for answer
                while (answer == null) {
                    Thread.yield();
                }
                checkAnswer();
            }
        }.start();
    }

    /**
     * check player answer
     */
    private void checkAnswer() {
        if (answer.equals("pass") || answer.equals("noActivity")) {
            roundPlayers--;
            setPlayerPassesScene();
        }
        else {
            playerAnswersUI();
            evaluateAnswer();
            setAnswerVerdictScene();
        }
    }

    /**
     * evaluate player answer
     */
    private void evaluateAnswer() {
        if (correctAnswer()) {
            roundScore += myTurn() ? 1 : 0;
            correctAnswerUI();
        }
        else {
            roundPlayers--;
            roundScore = myTurn() ? 0 : roundScore;
            wrongAnswerUI();
        }
    }

    /**
     * check answer correctness
     *
     * @return true if answer is correct
     */
    private boolean correctAnswer() {
        return answer.equalsIgnoreCase(questionAnswers[answerID]);
    }


    /**
     * Set the UI for wrong answer
     */
    private void wrongAnswerUI() {
        correctAnswer.setTextColor(Color.RED);
        answerVerdict.setText(String.format(getApplicationContext().getResources().getString(R.string.wrong_answer), playerOnMove));
        playersInRound.setText(String.format(getApplicationContext().getResources().getString(R.string.players_in_round), roundPlayers, gamePlayers));
    }

    /**
     * Set the UI for correct answer
     */
    private void correctAnswerUI() {
        correctAnswer.setTextColor(Color.GREEN);
        answerVerdict.setText(String.format(getApplicationContext().getResources().getString(R.string.correct_answer), playerOnMove));
        playerScore.setText(String.format(getApplicationContext().getResources().getString(R.string.round_score), roundScore));
    }

    /**
     * Set the UI for player answers
     */
    private void playerAnswersUI() {
        questionDetailText.setText(questions[answerID].getText().toString());
        answerInput.setText(answer);
        correctAnswer.setText(questionAnswers[answerID]);
        answers[answerID].setText(questionAnswers[answerID]);
    }

    /**
     * Set the UI for Question scene
     */
    private void setQuestionScene() {
        questionDetailLayout.setVisibility(RelativeLayout.GONE);
        nextPlayerLayout.setVisibility(RelativeLayout.GONE);
        questionLayout.setVisibility(RelativeLayout.VISIBLE);
        questionScoreLayout.setVisibility(LinearLayout.VISIBLE);
    }

    /**
     * Set the UI for Game Joining scene
     */
    private void setGameJoiningScene() {
        startingContent.setVisibility(RelativeLayout.GONE);
        gameContent.setVisibility(RelativeLayout.VISIBLE);

        gameJoiningLayout.setVisibility(RelativeLayout.VISIBLE);
        gameCodeLayout.setVisibility(RelativeLayout.VISIBLE);
        Arrays.stream(joinedPlayers).forEach(player -> player.setVisibility(TextView.GONE));
    }

    /**
     * set the UI for the starting scene
     */
    private void setStartingScene() {
        startingContent.setVisibility(RelativeLayout.VISIBLE);
        gameContent.setVisibility(RelativeLayout.GONE);

        if (playerName.equals("")) {
            setChangeNameScene();
        }
        else {
            startingLayout.setVisibility(RelativeLayout.VISIBLE);
            changeNameLayout.setVisibility(RelativeLayout.GONE);
            joinGameLayout.setVisibility(RelativeLayout.GONE);

            startingButtons.setVisibility(RelativeLayout.VISIBLE);
            friendGameButtons.setVisibility(RelativeLayout.GONE);
            createGameButtons.setVisibility(RelativeLayout.GONE);
        }
    }

    /**
     * Set the UI for the change name scene
     */
    private void setChangeNameScene() {
        startingLayout.setVisibility(RelativeLayout.GONE);
        changeNameLayout.setVisibility(RelativeLayout.VISIBLE);
        changePlayerNameInput.setText("");
        changeName.setEnabled(false);
    }

    /**
     * Set the UI for the join game scene
     */
    private void setJoinGameScene() {
        confirmCode.setEnabled(false);
        startingLayout.setVisibility(RelativeLayout.GONE);
        joinGameLayout.setVisibility(RelativeLayout.VISIBLE);
    }

    /**
     * Set the UI for the answer scene
     */
    private void setAnswerScene() {
        for (int i = 0; i < 10; i++) {
            answers[i].setText(questionAnswers[i]);
        }

        questionText.setText(R.string.question_solution);
        answerVerdictLayout.setVisibility(RelativeLayout.GONE);
        playerPassesLayout.setVisibility(RelativeLayout.GONE);
        questionDetailLayout.setVisibility(RelativeLayout.GONE);
        passQuestion.setVisibility(RelativeLayout.GONE);

        questionLayout.setVisibility(RelativeLayout.VISIBLE);
        playersInRound.setText("");
        playerScore.setText("");
        answersTimer();
    }

    /**
     * Configure the timer for the answer scene
     */
    private void answersTimer() {
        new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                questionTimer.setText(String.valueOf((int) (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                while (!playerScoreLoaded) {
                    Thread.yield();
                }
                playerScoreLoaded = false;
                passQuestion.setVisibility(RelativeLayout.VISIBLE);
                setPlayerScoreScene();
            }
        }.start();
    }

    /**
     * Set the UI for the player score scene
     */
    private void setPlayerScoreScene() {
        questionLayout.setVisibility(RelativeLayout.GONE);
        playersScoreLayout.setVisibility(RelativeLayout.VISIBLE);

        if (gameFinished()) {
            playersScoreTimer.setText("");
            setGameFinishedScene();
        }
        else {
            initializeRoundUI();
            readyForNextRound = true;
            playerScoreTimer();
        }
    }

    /**
     * Configure the timer for the player score scene
     */
    private void playerScoreTimer() {
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                playersScoreTimer.setText(String.valueOf((int) (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                while (playerOnMove.equals("roundFinished")) {
                    Thread.yield();
                }
                setNextPlayerScene();
            }
        }.start();
    }

    /**
     * Set the UI for game finished scene
     */
    private void setGameFinishedScene() {
        // SINGLE PLAYER GAME
        if (gameCode.equals("")) {
            playAgain.setVisibility(RelativeLayout.VISIBLE);
            playAgain.setText(R.string.new_game);
        }
        // FRIEND GAME
        else {
            if (gamePlayers == 1) {
                playAgain.setVisibility(RelativeLayout.GONE);
            }
            else {
                playAgain.setVisibility(RelativeLayout.VISIBLE);
                playAgain.setText(R.string.play_again);
            }
        }

        // END GAME BUTTON
        endGame.setVisibility(RelativeLayout.VISIBLE);
    }

    /**
     * Initialize components of the UI
     */
    private void initialize() {
        initializeTextViews();
        initializeButtons();
        initializeImageViews();
        initializeEditTexts();
        initializeLayouts();
        initializePreferences();
        initializeConnectivityManager();
        initializeConnectivityReceiver();
        initializeInputMethodManager();
    }

    /**
     * Initialize Connectivity Receiver for monitoring network connection
     */
    private void initializeConnectivityReceiver() {
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, intentFilter);
    }


    /**
     * Initialize the InputMethodManager
     */
    private void initializeInputMethodManager() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * Initialize the TextViews
     */
    private void initializeTextViews() {
        // starting scene
        playerNameTitle = findViewById(R.id.player_name_title);
        playerCounts = new TextView[]{findViewById(R.id.player_count1), findViewById(R.id.player_count2), findViewById(R.id.player_count3), findViewById(R.id.player_count4)};
        playerCountSelected = playerCounts[1];


        // game joining
        generatedGameCode = findViewById(R.id.generated_game_code);
        joinedPlayersCount = findViewById(R.id.joined_players_count);
        joinedPlayers = new TextView[]{findViewById(R.id.joined_player1), findViewById(R.id.joined_player2), findViewById(R.id.joined_player3), findViewById(R.id.joined_player4), findViewById(R.id.joined_player5)};
        joinedGameTimer = findViewById(R.id.joined_game_timer);

        // question scene
        questionText = findViewById(R.id.question_text);
        questions = new TextView[]{findViewById(R.id.question1), findViewById(R.id.question2), findViewById(R.id.question3), findViewById(R.id.question4), findViewById(R.id.question5), findViewById(R.id.question6), findViewById(R.id.question7), findViewById(R.id.question8), findViewById(R.id.question9), findViewById(R.id.question10)};
        answers = new TextView[]{findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4), findViewById(R.id.answer5), findViewById(R.id.answer6), findViewById(R.id.answer7), findViewById(R.id.answer8), findViewById(R.id.answer9), findViewById(R.id.answer10)};
        questionTimer = findViewById(R.id.question_timer);

        playersInRound = findViewById(R.id.players_in_round);
        playerScore = findViewById(R.id.player_score);

        // detail question scene
        questionDetailText = findViewById(R.id.question_detail_text);
        answerQuestionTimer = findViewById(R.id.answer_question_timer);

        // next player scene
        nextPlayerName = findViewById(R.id.next_player_name);

        // answer verdict scene
        correctAnswer = findViewById(R.id.correct_answer);
        answerVerdict = findViewById(R.id.answer_verdict);

        // player passes scene
        playerPassesName = findViewById(R.id.player_passes_name);

        // player score scene
        playerNames = new TextView[]{findViewById(R.id.first_player_name), findViewById(R.id.second_player_name), findViewById(R.id.third_player_name), findViewById(R.id.fourth_player_name), findViewById(R.id.fifth_player_name)};
        playerScores = new TextView[]{findViewById(R.id.first_player_score), findViewById(R.id.second_player_score), findViewById(R.id.third_player_score), findViewById(R.id.fourth_player_score), findViewById(R.id.fifth_player_score)};
        playersScoreTimer = findViewById(R.id.players_score_timer);
    }

    /**
     * Initialize the Layouts
     */
    private void initializeLayouts() {
        initializeStartingLayouts();
        initializeGameLayouts();
    }

    /**
     * Initialize the starting layouts
     */
    private void initializeStartingLayouts() {
        navbar = findViewById(R.id.navbar);
        startingLayout = findViewById(R.id.starting_layout);
        changeNameLayout = findViewById(R.id.change_name_layout);
        joinGameLayout = findViewById(R.id.join_game_layout);

        startingButtons = findViewById(R.id.starting_buttons);
        friendGameButtons = findViewById(R.id.friend_game_buttons);
        createGameButtons = findViewById(R.id.create_game_buttons);
    }

    /**
     * Initialize the game layouts
     */
    private void initializeGameLayouts() {
        gameJoiningLayout = findViewById(R.id.game_joining_layout);
        gameCodeLayout = findViewById(R.id.game_code_layout);
        questionLayout = findViewById(R.id.question_layout);
        questionDetailLayout = findViewById(R.id.question_detail_layout);
        nextPlayerLayout = findViewById(R.id.next_player_layout);
        playersScoreLayout = findViewById(R.id.players_score_layout);
        playerPassesLayout = findViewById(R.id.player_passes_layout);

        questionScoreLayout = findViewById(R.id.question_score_layout);
        answerLayout = findViewById(R.id.answer_layout);
        answerVerdictLayout = findViewById(R.id.answer_verdict_layout);
    }

    /**
     * Initialize the Action Listeners
     */
    private void actions() {
        textViewsActions();
        buttonsActions();
        imageViewsActions();
        editTextsActions();
    }

    /**
     * Initialize the TextViews actions
     */
    private void textViewsActions() {
        for (TextView playerCount : playerCounts) {
            playerCount.setOnClickListener(view -> {
                playerCountSelected.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
                playerCount.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
                playerCountSelected = playerCount;
            });
        }

        for (int i = 0; i < answers.length; i++) {
            int finalI = i;
            answers[i].setOnClickListener(view -> {
                myAnswerID = finalI;
                answerInput.setText("");
                questionDetailText.setText(questions[finalI].getText().toString());
                setQuestionDetailScene();
            });
        }
    }

    /**
     * set the question detail scene UI
     */
    private void setQuestionDetailScene() {
        questionLayout.setVisibility(View.GONE);
        questionDetailLayout.setVisibility(View.VISIBLE);
        answerLayout.setVisibility(View.VISIBLE);
        answerQuestion.setEnabled(false);
    }

    /**
     * initialize ImageView actions
     */
    private void imageViewsActions() {
        homeIconActions();
        menuIconActions();
    }

    /**
     * set the home icon actions
     */
    private void homeIconActions() {
        homeIcon.setOnClickListener(view -> setStartingScene());
    }

    /**
     * set the menu icon actions
     */
    private void menuIconActions() {
        menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(Desitka.this, findViewById(R.id.navbar), Gravity.END);
            popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.change_name) {
                    setChangeNameScene();
                    return true;
                }
                else if (itemId == R.id.about_game) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DAVIDACEK4689/Desitka"));
                    startActivity(browserIntent);
                    return true;
                }
                else {
                    return false;
                }
            });

            popupMenu.show();
        });
    }

    /**
     * set the edit texts actions
     */
    private void editTextsActions() {
        createPlayerNameActions();
        gameCodeActions();
        answerActions();
    }

    /**
     * set the answer actions
     */
    private void answerActions() {
        answerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                answerQuestion.setEnabled(answerInput.getText().toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * set the create player name actions
     */
    private void createPlayerNameActions() {
        changePlayerNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeName.setEnabled(changePlayerNameInput.getText().toString().trim().length() != 0);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeName.setEnabled(changePlayerNameInput.getText().toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * set the game code actions
     */
    private void gameCodeActions() {
        gameCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmCode.setEnabled(gameCodeInput.getText().toString().trim().length() == 8);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Initialize the buttons actions
     */
    private void buttonsActions() {
        singlePlayerAction();
        multiplayerAction();
        setUpGameAction();
        joinGameAction();
        createGameAction();
        changeNameAction();
        confirmCodeAction();
        returnToMenu2Action();

        passQuestionAction();
        answerQuestionAction();
        returnToQuestionAction();
        playAgainAction();
        returnToMenu3Action();
    }


    /**
     * set the single player button action
     */
    private void singlePlayerAction() {
        singlePlayer.setOnClickListener(view -> {
            if (!isInternetAvailable()) {
                requestInternetConnection();
            }
            else {
                setGameJoiningScene();
                gameCodeLayout.setVisibility(RelativeLayout.GONE);
                clientThread = new Thread(new ClientThread("singleplayer"));
                clientThread.start();
            }
        });
    }

    /**
     * set the multiplayer button action
     */
    private void multiplayerAction() {
        multiplayer.setOnClickListener(view -> {
            if (!isInternetAvailable()) {
                requestInternetConnection();
            }
            else {
                startingButtons.setVisibility(RelativeLayout.GONE);
                friendGameButtons.setVisibility(RelativeLayout.VISIBLE);
            }
        });
    }

    /**
     * set the set up game button action
     */
    private void setUpGameAction() {
        setUpGame.setOnClickListener(view -> {
            friendGameButtons.setVisibility(RelativeLayout.GONE);
            createGameButtons.setVisibility(RelativeLayout.VISIBLE);
        });
    }

    /**
     * set the join game button action
     */
    private void joinGameAction() {
        joinGame.setOnClickListener(view -> {
            gameCodeInput.setText("");
            setJoinGameScene();
        });
    }

    /**
     * set the create game button action
     */
    private void createGameAction() {
        createGame.setOnClickListener(view -> {
            setGameJoiningScene();
            clientThread = new Thread(new ClientThread("createGame"));
            clientThread.start();
        });
    }

    /**
     * set the change name button action
     */
    private void changeNameAction() {
        changeName.setOnClickListener(view -> {
            updatePlayerName(changePlayerNameInput.getText().toString());
            navbar.setVisibility(RelativeLayout.VISIBLE);
            setStartingScene();

            inputMethodManager.hideSoftInputFromWindow(changePlayerNameInput.getWindowToken(), 0);
        });

    }

    /**
     * set the confirm code button action
     */
    private void confirmCodeAction() {
        confirmCode.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(gameCodeInput.getWindowToken(), 0);
            clientThread = new Thread(new ClientThread("joinGame"));
            clientThread.start();
        });
    }

    /**
     * set the return to menu button action
     */
    private void returnToMenu2Action() {
        returnToMenu.setOnClickListener(view -> {
            startingLayout.setVisibility(RelativeLayout.VISIBLE);
            joinGameLayout.setVisibility(RelativeLayout.GONE);
            inputMethodManager.hideSoftInputFromWindow(gameCodeInput.getWindowToken(), 0);
        });
    }

    /**
     * set the pass question button action
     */
    private void passQuestionAction() {
        passQuestion.setOnClickListener(view -> myAnswer = "pass");
    }

    /**
     * set the answer question button action
     */
    private void answerQuestionAction() {
        answerQuestion.setOnClickListener(view -> {
            myAnswer = answerInput.getText().toString().trim();
            inputMethodManager.hideSoftInputFromWindow(gameCodeInput.getWindowToken(), 0);
        });
    }

    /**
     * set the return to question button action
     */
    private void returnToQuestionAction() {
        returnToQuestion.setOnClickListener(view -> {
            setQuestionScene();
            answerInput.setText("");
            inputMethodManager.hideSoftInputFromWindow(gameCodeInput.getWindowToken(), 0);
        });
    }

    /**
     * set the play again button action
     */
    private void playAgainAction() {
        playAgain.setOnClickListener(view -> {
            // HIDE OLD SCENE
            playersScoreLayout.setVisibility(RelativeLayout.GONE);

            if (gameCode.equals("")) {
                singlePlayer.performClick();
            }
            else {
                confirmCode.performClick();
            }
        });

    }

    /**
     * set the return to menu button action
     */
    private void returnToMenu3Action() {
        endGame.setOnClickListener(view -> {
            setStartingScene();
            playAgain.setVisibility(RelativeLayout.GONE);
            endGame.setVisibility(RelativeLayout.GONE);
            playersScoreLayout.setVisibility(RelativeLayout.GONE);
        });
    }

    /**
     * Initialize the buttons
     */
    private void initializeButtons() {
        initializeStartingButtons();
        initializeGameButtons();
    }

    /**
     * Initialize the starting buttons
     */
    private void initializeStartingButtons() {
        singlePlayer = findViewById(R.id.single_player_button);
        multiplayer = findViewById(R.id.multiplayer_button);
        setUpGame = findViewById(R.id.setup_game_button);
        joinGame = findViewById(R.id.join_game_button);
        createGame = findViewById(R.id.create_game_button);
        changeName = findViewById(R.id.change_name_button);
        confirmCode = findViewById(R.id.confirm_code_button);
        returnToMenu = findViewById(R.id.return_to_menu);
    }

    /**
     * Initialize the game buttons
     */
    private void initializeGameButtons() {
        passQuestion = findViewById(R.id.pass_question_button);
        answerQuestion = findViewById(R.id.answer_question_button);
        returnToQuestion = findViewById(R.id.return_to_question_button);
        playAgain = findViewById(R.id.play_again);
        endGame = findViewById(R.id.end_game);
    }

    /**
     * Initialize the ImageViews
     */
    private void initializeImageViews() {
        homeIcon = findViewById(R.id.home_icon);
        menuIcon = findViewById(R.id.menu_icon);
    }

    /**
     * Initialize the EditTexts
     */
    private void initializeEditTexts() {
        initializeStartingEditTexts();
        initializeGameEditTexts();
    }

    /**
     * Initialize the starting StartingEditTexts
     */
    private void initializeStartingEditTexts() {
        changePlayerNameInput = findViewById(R.id.change_player_name_input);
        gameCodeInput = findViewById(R.id.game_code_input);
    }

    /**
     * Initialize the game GameEditTexts
     */
    private void initializeGameEditTexts() {
        answerInput = findViewById(R.id.answer_input);
    }

    /**
     * initialization of internet network service
     */
    private void initializeConnectivityManager(){
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /**
     * method to check whether internet connection is enabled
     *
     * @return boolean value
     */
    private boolean isInternetAvailable(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * method to check whether it is player's turn
     *
     * @return true if it is player's turn, false otherwise
     */
    private boolean myTurn() {
        return playerOnMove.equals(playerName);
    }


    /**
     * alert dialog requesting internet connection
     */
    private void requestInternetConnection() {
        alertDialogBuilder = new AlertDialog.Builder(Desitka.this);
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Aplikace vyžaduje pro správné fungování internetové připojení")
                .setNeutralButton("OK", (dialog, id) -> {
                    dialog.cancel();
                    setStartingScene();
                });
        alert = alertDialogBuilder.create();
        alert.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
    }


    /**
     * Client thread to connect and communicate with server
     */
    class ClientThread implements Runnable {
        private Socket client;
        private BufferedReader bufferedReader;
        private PrintWriter printwriter;

        private final String gameRequest;

        /**
         * Constructor
         *
         * @param gameRequest game type request
         */
        public ClientThread(String gameRequest) {
            this.gameRequest = gameRequest;
        }

        /**
         * connect to server and communicate with it
         */
        @Override
        public void run() {
            try {
                client = new Socket("7.tcp.eu.ngrok.io", 18785); // connect to server
                bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                printwriter = new PrintWriter(client.getOutputStream(),true);

                // SEARCHING FOR GAME
                printwriter.println(playerName);
                printwriter.println(gameRequest);
                boolean gameFound = searchGame();

                // IF GAME NOT FOUND
                if (!gameFound) {
                    printwriter.close();
                    bufferedReader.close();
                    client.close();
                    return;
                }

                waitForPlayers();
                startGame();

                printwriter.close();
                bufferedReader.close();
                client.close();
            }
            catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Start game round
         */
        private void startRound() {
            try {
                initializeRound();
                loadQuestion();
                playRound();
                loadPlayerScore();
            }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * initialize round
         */
        private void initializeRound() {
            answer = null;
            answerRecorded = false;
            playerOnMoveUpdated = false;
            playerScoreLoaded = false;
            readyForNextRound = false;
        }

        /**
         * load player Score
         *
         * @throws IOException IOException
         * @throws InterruptedException InterruptedException
         */
        private void loadPlayerScore() throws IOException, InterruptedException {
            gamePlayers = Integer.parseInt(readLine());
            gameFinished = Boolean.parseBoolean(readLine());

            for (int i = 0; i < gamePlayers; i++) {
                playerNames[i].setText(readLine());
                playerScores[i].setText(readLine());
                playerNames[i].setVisibility(RelativeLayout.VISIBLE);
                playerScores[i].setVisibility(RelativeLayout.VISIBLE);
            }

            for (int i = gamePlayers; i < playerNames.length; i++) {
                playerNames[i].setVisibility(RelativeLayout.GONE);
                playerScores[i].setVisibility(RelativeLayout.GONE);
            }
            playerScoreLoaded = true;
        }

        /**
         * play round
         *
         * @throws IOException IOException
         * @throws InterruptedException InterruptedException
         */
        private void playRound() throws IOException, InterruptedException {
            playerOnMove = readLine();

            while (!roundFinished()) {
                getPlayerAnswer();
                scheduleNextPlayer();
            }
        }

        /**
         * Load player answer from server
         *
         * @throws IOException IOException
         */
        private void getPlayerAnswer() throws IOException {
            if (myTurn()) {
                answerQuestion();
            }
            answerID = Integer.parseInt(readLine());
            answer = readLine();
        }

        /**
         * Answer question if my turn
         */
        private void answerQuestion() {
            while (myAnswer == null) {
                Thread.yield();
            }
            printwriter.println(myAnswerID);
            printwriter.println(myAnswer);
            myAnswer = null;
        }

        /**
         * Schedule next player
         *
         * @throws IOException IOException
         */
        private void scheduleNextPlayer() throws IOException {
            while (!answerRecorded) {
                Thread.yield();
            }
            answerRecorded = false;

            // load next player
            playerOnMove = readLine();
            playerOnMoveUpdated = true;
        }

        /**
         * Load question from server when starting new round
         *
         * @throws IOException IOException
         * @throws InterruptedException InterruptedException
         */
        private void loadQuestion() throws IOException, InterruptedException {
            questionText.setText(readLine());
            for (int i = 0; i < questions.length; i++) {
                questions[i].setText(readLine());
                questionAnswers[i] = readLine();
                answers[i].setText("?");
            }
        }

        /**
         * play game
         */
        private void playGame() {
            while (!gameFinished()) {
                while (!readyForNextRound) {
                    Thread.yield();
                }
                startRound();
            }
        }

        /**
         * start game
         */
        private void startGame() {
            playersFound = true;
            playerOnMove = "gameStarted";
            readyForNextRound = true;
            gameFinished = false;

            playGameUI();
            playGame();
        }

        /**
         * play game UI
         */
        private void playGameUI() {
            runOnUiThread(() -> {
                initializeRoundUI();
                startGameTimer();
            });
        }

        /**
         * configure start game timer
         */
        private void startGameTimer() {
            new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    joinedGameTimer.setText(String.format(getApplicationContext().getResources().getString(R.string.game_start), millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    while (playerOnMove.equals("gameStarted")) {
                        Thread.yield();
                    }
                    setNextPlayerScene();
                }
            }.start();
        }

        /**
         * wait for other players to join
         *
         * @throws IOException IOException
         * @throws InterruptedException InterruptedException
         */
        private void waitForPlayers() throws IOException, InterruptedException {
            startWaitingTimer();

            // WAITING FOR OTHER PLAYERS
            for (int i = 0; i < gamePlayers; i++) {
                String joinedPlayer = readLine();
                int player_index = i;

                runOnUiThread(() -> {
                    joinedPlayersCount.setText(String.format(getApplicationContext().getResources().getString(R.string.joined_players), player_index+1, gamePlayers));
                    joinedPlayers[player_index].setText(joinedPlayer);
                    joinedPlayers[player_index].setVisibility(View.VISIBLE);
                });
            }
        }

        /**
         * Wait max 60 seconds for other players to join
         *
         * @throws IOException IOException
         */
        private void startWaitingTimer() throws IOException {
            playersFound = false;
            long currentTime = System.currentTimeMillis();
            long gameCreationTime = Long.parseLong(readLine());
            long timeToWait = gameCreationTime + 60000 - currentTime;

            runOnUiThread(() -> new CountDownTimer(timeToWait, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (playersFound) {
                        this.cancel();
                    }
                    joinedGameTimer.setText(String.format(getApplicationContext().getResources().getString(R.string.waiting_time), millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    alertDialogBuilder = new AlertDialog.Builder(Desitka.this);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setMessage("Hráči nenalezeni")
                            .setNeutralButton("OK", (dialog, id) -> {
                                dialog.cancel();
                                setStartingScene();
                            });
                    alert = alertDialogBuilder.create();
                    alert.show();
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);

                }
            }.start());
        }

        /**
         * read line from server
         *
         * @return String from server
         * @throws IOException IOException
         */
        private String readLine() throws IOException {
            while (!bufferedReader.ready()){
                Thread.yield();
            }
            return bufferedReader.readLine();
        }

        /**
         * search game
         *
         * @return true if game found
         * @throws IOException IOException
         * @throws InterruptedException InterruptedException
         */
        private boolean searchGame() throws IOException, InterruptedException {
            if (gameRequest.equals("singleplayer")) {
                gameCode = "";
                gamePlayers = 3;
                return true;
            }
            else if (gameRequest.equals("createGame")) {
                printwriter.println(playerCountSelected.getText().toString());
                gameCode = readLine();

                // FOR USERS WHO PLAYS FRIEND GAME AGAIN (GAME HAS THE SAME CODE)
                gameCodeInput.setText(gameCode);

                runOnUiThread(() -> generatedGameCode.setText(gameCode));
                gamePlayers = Integer.parseInt(playerCountSelected.getText().toString());
                return true;
            }
            else {
                printwriter.println(gameCodeInput.getText().toString());
                String response = readLine();

                switch (response) {
                    case "joined":
                        runOnUiThread(() -> {
                            setGameJoiningScene();
                            gameCode = gameCodeInput.getText().toString();
                            generatedGameCode.setText(gameCode);
                        });
                        gamePlayers = Integer.parseInt(readLine());
                        return true;

                    case "nonExistingGame":
                        runOnUiThread(() -> {
                            alertDialogBuilder = new AlertDialog.Builder(Desitka.this);
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setMessage("Nesprávný kód hry")
                                    .setNeutralButton("OK", (dialog, id) -> {
                                            dialog.cancel();
                                            setStartingScene();
                                    });
                            alert = alertDialogBuilder.create();
                            alert.show();
                            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        });
                        return false;

                    case "nameAlreadyJoined":
                        runOnUiThread(() -> {
                            alertDialogBuilder = new AlertDialog.Builder(Desitka.this);
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setMessage("Hráč s Vaším jménem již hraje")
                                    .setNeutralButton("OK", (dialog, id) -> {
                                                dialog.cancel();
                                                setStartingScene();
                                    });
                            alert = alertDialogBuilder.create();
                            alert.show();
                            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        });
                        return false;
                }
            }
            return false;
        }
    }

    /**
     * initialize round UI
     */
    private void initializeRoundUI() {
        roundScore = 0;
        roundPlayers = gamePlayers;

        playersInRound.setText(String.format(getApplicationContext().getResources().getString(R.string.players_in_round), roundPlayers, gamePlayers));
        playerScore.setText(String.format(getApplicationContext().getResources().getString(R.string.round_score), roundScore));
    }

    /**
     * set answer Verdict scene
     */
    private void setAnswerVerdictScene() {
        questionLayout.setVisibility(View.GONE);
        answerLayout.setVisibility(View.GONE);
        questionDetailLayout.setVisibility(View.VISIBLE);
        answerVerdictLayout.setVisibility(View.VISIBLE);
        answerVerdictTimer();
    }

    /**
     * configure answer verdict timer
     */
    private void answerVerdictTimer() {
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                questionDetailLayout.setVisibility(View.GONE);
                answerVerdictLayout.setVisibility(View.GONE);
                setNextScene();
            }
        }.start();
    }

    /**
     * set Player Passes scene
     */
    private void setPlayerPassesScene() {
        playersInRound.setText(String.format(getApplicationContext().getResources().getString(R.string.players_in_round), roundPlayers, gamePlayers));
        playerPassesName.setText(String.format(getApplicationContext().getResources().getString(R.string.player_passes), playerOnMove));

        questionLayout.setVisibility(View.GONE);
        questionDetailLayout.setVisibility(View.GONE);
        playerPassesLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(this::setNextScene, 5000);
    }

    /**
     * set Next Player scene
     */
    private void setNextScene() {
        waitForNextPlayer();
        if (roundFinished()) {
            setAnswerScene();
        }
        else {
            setNextPlayerScene();
        }
    }

    /**
     * wait for next player
     */
    private void waitForNextPlayer() {
        answer = null;
        answerRecorded = true;

        while (!playerOnMoveUpdated) {
            Thread.yield();
        }
        playerOnMoveUpdated = false;
    }

    /**
     * check if game is finished
     *
     * @return true if game is finished
     */
    private boolean gameFinished() {
        return gameFinished;
    }

    /**
     * check if round is finished
     *
     * @return true if round is finished
     */
    private boolean roundFinished() {
        return playerOnMove.equals("roundFinished");
    }
}