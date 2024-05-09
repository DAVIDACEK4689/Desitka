package cz.cuni.mff.desitka.client.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.gameLogic.GameViewModel;

/**
 * The GameActivity class represents the game activity of the application.
 */
public class GameActivity extends AppCompatActivity implements GameStates {
    private GameViewModel gameViewModel;
    private FragmentManager fragmentManager;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private TextView timerText;
    private OnBackPressedCallback callback;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-constructed from a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        initialize();
        addObservers();
        registerBackPressedCallback();
        handleScreenOrientation();
        startScene(savedInstanceState);
    }

    /**
     * Starts the scene.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    private void startScene(Bundle savedInstanceState) {
        if (!isInternetConnected()) {
            gameViewModel.reportConnectionError(R.string.no_internet_connection);
            return;
        }
        loadScene(savedInstanceState);
    }

    /**
     * Loads the scene.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    private void loadScene(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initializeFragments();
            gameViewModel.initializeGameModel(getApplicationContext());
            gameViewModel.startGame(getIntent());
        }
    }

    /**
     * Handles the screen orientation based on the screen size.
     */
    private void handleScreenOrientation() {
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Registers a callback to be invoked when the back button is pressed.
     */
    private void registerBackPressedCallback() {
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (gameViewModel.getDisplayedGameState() == GameState.PLAYER_ANSWERS_DETAIL) {
                    gameViewModel.getGameStateChange().postValue(GameState.PLAYER_ANSWERS);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Adds observers to the game state, timer text, and connection error.
     */
    private void addObservers() {
        addGameStateObserver();
        addTimerTextObserver();
        addConnectionErrorObserver();
        addNetworkCallback();
    }

    /**
     * Initializes the game view model, fragment manager, connectivity manager, and timer text.
     */
    private void initialize() {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        fragmentManager = getSupportFragmentManager();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        timerText = findViewById(R.id.game_timer);
    }

    /**
     * Initializes the fragments of the activity.
     */
    private void initializeFragments() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (String fragmentTag : getFragmentTags()) {
            try {
                String fragmentName = "cz.cuni.mff.desitka.client.fragments.game." + fragmentTag;
                Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);
                Fragment fragment = fragmentClass.newInstance();
                transaction.add(R.id.game_frame, fragment, fragmentTag)
                           .hide(fragment);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        transaction.commitNow();
    }

    /**
     * Gets the fragment tags.
     *
     * @return A set of fragment tags.
     */
    private Set<String> getFragmentTags() {
        return Stream.of(GameState.values())
                .map(this::getFragmentTag)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the fragment tag for a given game state.
     *
     * @param gameState The game state.
     * @return The fragment tag.
     */
    private String getFragmentTag(GameState gameState) {
        switch (gameState) {
            case GAME_JOINING:
                return "ServerWaiting";
            case SHOW_ANSWER_WAITING:
                return "ServerWaiting";
            case WAITING_FOR_PLAYERS:
            case GAME_START:
                return "GameStart";
            case ROUND_START:
                return "RoundStart";
            case ROUND_PLAYER:
                return "RoundPlayer";
            case PLAYER_ANSWERS:
                return "Question";
            case SHOW_ANSWERS:
                return "Question";
            case PLAYER_ANSWERS_DETAIL:
                return "QuestionDetail";
            case SHOW_ANSWER:
                return "Answer";
            case ROUND_END:
                return "Score";
            case GAME_END:
                return "Score";
        }
        throw new IllegalArgumentException();
    }

    /**
     * Adds an observer to the game state.
     */
    private void addGameStateObserver() {
        gameViewModel.getGameStateChange().observe(this, this::changeGameFragment);
    }

    /**
     * Adds an observer to the timer text.
     */
    private void addTimerTextObserver() {
        gameViewModel.getTimerText().observe(this, timerText::setText);
    }

    /**
     * Adds an observer to the connection error.
     */
    private void addConnectionErrorObserver() {
        gameViewModel.getConnectionError().observe(this, this::showAlertDialog);
    }

    /**
     * Adds a network callback.
     */
    private void addNetworkCallback() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                showAlertDialog(R.string.no_internet_connection);
            }
        };
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }

    /**
     * Checks if the internet is connected.
     *
     * @return true if the internet is connected, false otherwise.
     */
    private boolean isInternetConnected() {
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities networkCap = connectivityManager.getNetworkCapabilities(network);
            return networkCap != null && networkCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    /**
     * Changes the game fragment.
     *
     * @param newState The new state of the game.
     */
    private void changeGameFragment(GameState newState) {
        hideAndShowFragment(newState);
        gameViewModel.updateGameState(newState);
    }

    /**
     * Hides the current fragment and shows a new one.
     *
     * @param newState The new state of the game.
     */
    private void hideAndShowFragment(GameState newState) {
        GameState oldState = gameViewModel.getDisplayedGameState();
        String oldTag = getFragmentTag(oldState);
        String newTag = getFragmentTag(newState);

        fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(oldTag))
                .show(fragmentManager.findFragmentByTag(newTag))
                .commit();
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBackPressedCallback();
        unregisterNetworkCallback();
    }

    /**
     * Unregisters the callback to be invoked when the back button is pressed.
     */
    private void unregisterBackPressedCallback() {
        if (callback != null) {
            callback.remove();
        }
    }

    /**
     * Shows an alert dialog with a specific error message.
     *
     * @param errorID The ID of the error message.
     */
    public void showAlertDialog(int errorID) {
        if (errorID != -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage(errorID)
                    .setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            button.setTextColor(ContextCompat.getColor(this, R.color.primaryColor));
            button.setOnClickListener(v -> {
                alertDialog.dismiss();
                Intent intent = new Intent(this, StartingActivity.class);
                this.finish();
                startActivity(intent);
            });
        }
    }

    /**
     * Unregisters the network callback.
     */
    private void unregisterNetworkCallback() {
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}