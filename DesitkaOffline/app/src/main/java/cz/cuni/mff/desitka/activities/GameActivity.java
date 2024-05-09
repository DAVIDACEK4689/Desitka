package cz.cuni.mff.desitka.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.logic.game.GameViewModel;
import cz.cuni.mff.desitka.logic.game.GameConstants;
import cz.cuni.mff.desitka.interfaces.GameStates;

/**
 * Represents the game activity in the game.
 * This class is responsible for managing the game state of the game.
 */
public class GameActivity extends AppCompatActivity implements GameStates {
    private GameViewModel gameViewModel;
    private FragmentManager fragmentManager;
    private CountDownTimer countDownTimer;
    private OnBackPressedCallback callback;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-created from a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        initialize();
        addGameStateObserver();
        startScene(savedInstanceState);
        registerBackPressedCallback();
        handleScreenOrientation();
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
                if (gameViewModel.getDisplayedGameFragment().getValue() == GameState.PLAYER_ANSWERS_DETAIL) {
                    gameViewModel.getGameFragmentChange().postValue(GameState.PLAYER_ANSWERS);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Initializes the game view model and fragment manager.
     */
    private void initialize() {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Adds observers to the game state.
     */
    private void addGameStateObserver() {
        gameViewModel.getDisplayedGameFragment().observe(this, this::handleCountDownTimer);
        gameViewModel.getGameFragmentChange().observe(this, this::changeGameFragment);
    }

    /**
     * Handles the countdown timer based on the game state.
     * @param gameState The current game state.
     */
    private void handleCountDownTimer(GameState gameState) {
        long sceneTime = getSceneTime(gameState);
        if (sceneTime != Long.MAX_VALUE) {
            gameViewModel.setSceneTime(sceneTime);
            startCountDownTimer(sceneTime);
        }
    }

    /**
     * Starts a countdown timer.
     * @param sceneTime The time for the scene.
     */
    private void startCountDownTimer(long sceneTime) {
        countDownTimer = new CountDownTimer(sceneTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameViewModel.setSceneTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                gameViewModel.setSceneTime(0L);
                gameViewModel.handleTimerFinish();
            }
        };
        countDownTimer.start();
    }

    /**
     * Gets the scene time based on the game state.
     * @param gameState The current game state.
     * @return The scene time.
     */
    private long getSceneTime(GameState gameState) {
        long sceneTime = gameViewModel.getSceneTime();
        if (sceneTime == 0) {
            sceneTime = GameConstants.getSceneTime(gameState);
        }
        return sceneTime;
    }

    /**
     * Changes the game fragment.
     * @param newState The new game state.
     */
    private void changeGameFragment(GameState newState) {
        hideAndShowFragment(newState);
        gameViewModel.getDisplayedGameFragment().setValue(newState);
    }

    /**
     * Starts the scene.
     * @param savedInstanceState The saved state of the activity.
     */
    private void startScene(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initializeFragments();
            gameViewModel.initializeGameController(getApplicationContext());
            gameViewModel.startGame(getIntent());
        }
    }

    /**
     * Initializes the fragments.
     */
    private void initializeFragments() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (String fragmentTag : getFragmentTags()) {
            try {
                String fragmentName = "cz.cuni.mff.desitka.fragments.game." + fragmentTag;
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
     * @return The set of fragment tags.
     */
    private Set<String> getFragmentTags() {
        return Stream.of(GameState.values())
                .map(this::getFragmentTag)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the fragment tag based on the game state.
     * @param gameState The current game state.
     * @return The fragment tag.
     */
    private String getFragmentTag(GameState gameState) {
        switch (gameState) {
            case GAME_START: return "GameStart";
            case ROUND_START: return "RoundStart";
            case ROUND_PLAYER: return "RoundPlayer";
            case PLAYER_ANSWERS: return "Question";
            case SHOW_ANSWERS: return "Question";
            case PLAYER_ANSWERS_DETAIL: return "QuestionDetail";
            case SHOW_ANSWER: return "Answer";
            case ROUND_END: return "Score";
            case GAME_END: return "Score";
        }
        throw new IllegalArgumentException();
    }

    /**
     * Hides the current fragment and shows the new one.
     * @param newState The new game state.
     */
    private void hideAndShowFragment(GameState newState) {
        GameState oldState = gameViewModel.getDisplayedGameFragment().getValue();
        String oldTag = getFragmentTag(oldState);
        String newTag = getFragmentTag(newState);

        fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(oldTag))
                .show(fragmentManager.findFragmentByTag(newTag))
                .commit();
    }

    /**
     * Cancels the timer.
     */
    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Called when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        handleCountDownTimer(gameViewModel.getDisplayedGameFragment().getValue());
    }

    /**
     * Called when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBackPressedCallback();
    }

    /**
     * Unregisters the callback to be invoked when the back button is pressed.
     */
    private void unregisterBackPressedCallback() {
        if (callback != null) {
            callback.remove();
        }
    }
}