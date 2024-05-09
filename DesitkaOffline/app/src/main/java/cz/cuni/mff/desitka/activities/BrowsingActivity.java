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
import cz.cuni.mff.desitka.interfaces.BrowsingStates;
import cz.cuni.mff.desitka.logic.browsing.BrowsingConstants;
import cz.cuni.mff.desitka.logic.browsing.BrowsingViewModel;

/**
 * Represents the browsing activity in the game.
 * This class is responsible for managing the browsing state of the game.
 */
public class BrowsingActivity extends AppCompatActivity implements BrowsingStates {
    private BrowsingViewModel browsingViewModel;
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
        setContentView(R.layout.browsing_activity);
        initialize();
        addBrowsingStateObserver();
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
                // Do nothing
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Initializes the browsing view model and fragment manager.
     */
    private void initialize() {
        browsingViewModel = new ViewModelProvider(this).get(BrowsingViewModel.class);
        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Adds observers to the browsing state.
     */
    private void addBrowsingStateObserver() {
        browsingViewModel.getDisplayedBrowsingFragment().observe(this, this::handleCountDownTimer);
        browsingViewModel.getBrowsingFragmentChange().observe(this, this::changeBrowsingState);
    }

    /**
     * Handles the countdown timer based on the browsing state.
     * @param browsingState The current browsing state.
     */
    private void handleCountDownTimer(BrowsingState browsingState) {
        long sceneTime = getSceneTime(browsingState);
        if (sceneTime != Long.MAX_VALUE) {
            browsingViewModel.setSceneTime(sceneTime);
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
                browsingViewModel.setSceneTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                browsingViewModel.getBrowsingFragmentChange().postValue(BrowsingState.BrowsingQuestion);
                browsingViewModel.getNextSubQuestion();
                browsingViewModel.setSceneTime(0L);
            }
        };
        countDownTimer.start();
    }

    /**
     * Gets the scene time based on the browsing state.
     * @param browsingState The current browsing state.
     * @return The scene time.
     */
    private long getSceneTime(BrowsingState browsingState) {
        long sceneTime = browsingViewModel.getSceneTime();
        if (sceneTime == 0) {
            sceneTime = BrowsingConstants.getSceneTime(browsingState);
        }
        return sceneTime;
    }

    /**
     * Changes the browsing state.
     * @param newState The new browsing state.
     */
    private void changeBrowsingState(BrowsingState newState) {
        hideAndShowFragment(newState);
        browsingViewModel.getDisplayedBrowsingFragment().setValue(newState);
    }

    /**
     * Starts the scene.
     * @param savedInstanceState The saved state of the activity.
     */
    private void startScene(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initializeFragments();
            browsingViewModel.initializeBrowsingController(getApplicationContext());
            browsingViewModel.chooseBrowsingQuestion();
        }
    }

    /**
     * Initializes the fragments.
     */
    private void initializeFragments() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (String fragmentTag : getFragmentTags()) {
            try {
                String fragmentName = "cz.cuni.mff.desitka.fragments.browsing." + fragmentTag;
                Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);
                Fragment fragment = fragmentClass.newInstance();
                transaction.add(R.id.browsing_frame, fragment, fragmentTag)
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
        return Stream.of(BrowsingState.values())
                .map(this::getFragmentTag)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the fragment tag based on the browsing state.
     * @param browsingState The current browsing state.
     * @return The fragment tag.
     */
    private String getFragmentTag(BrowsingState browsingState) {
        switch (browsingState) {
            case BrowsingStart: return "BrowsingStart";
            case BrowsingQuestion: return "BrowsingQuestion";
            case BrowsingAnswer: return "BrowsingAnswer";
        }
        throw new IllegalArgumentException();
    }

    /**
     * Hides the current fragment and shows the new one.
     * @param newState The new browsing state.
     */
    private void hideAndShowFragment(BrowsingState newState) {
        BrowsingState oldState = browsingViewModel.getDisplayedBrowsingFragment().getValue();
        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(oldState.name());
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(newState.name());

        fragmentManager.beginTransaction()
                .hide(oldFragment)
                .show(newFragment)
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
        handleCountDownTimer(browsingViewModel.getDisplayedBrowsingFragment().getValue());
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