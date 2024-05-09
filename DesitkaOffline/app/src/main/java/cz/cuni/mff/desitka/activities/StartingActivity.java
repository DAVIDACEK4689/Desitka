package cz.cuni.mff.desitka.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.interfaces.StartingStates;

/**
 * Represents the starting activity in the game.
 * This class is responsible for managing the starting state of the game.
 */
public class StartingActivity extends AppCompatActivity implements StartingStates {
    private StartingState displayedFragment;
    private OnBackPressedCallback callback;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-created from a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_activity);
        loadState(savedInstanceState);
        registerBackPressedCallback();
        handleScreenOrientation();
    }

    /**
     * Registers a callback to be invoked when the back button is pressed.
     */
    private void registerBackPressedCallback() {
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (displayedFragment == StartingState.FriendGamePage) {
                    replaceFragment(StartingState.WelcomePage);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
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
     * Loads the state of the activity.
     * @param savedInstanceState The saved state of the activity.
     */
    private void loadState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            displayedFragment = (StartingState) savedInstanceState.getSerializable("fragmentPhase");
            return;
        }
        displayedFragment = StartingState.WelcomePage;
        initializeFragments();
        startFragment();
    }

    /**
     * Initializes the fragments.
     */
    private void initializeFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (StartingState startingState : StartingState.values()) {
            Fragment fragment = createFragment(startingState);
            transaction.add(R.id.starting_frame, fragment, startingState.name())
                    .hide(fragment);
        }
        transaction.commitNow();
    }

    /**
     * Creates a fragment based on the starting state.
     * @param startingState The starting state.
     * @return The created fragment.
     */
    private Fragment createFragment(StartingState startingState) {
        try {
            String fragmentName = "cz.cuni.mff.desitka.fragments.starting." + startingState.name();
            Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);
            return fragmentClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts a fragment.
     */
    private void startFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(displayedFragment.name());
        getSupportFragmentManager().beginTransaction()
                .show(Objects.requireNonNull(fragment))
                .commitNow();
    }

    /**
     * Replaces the current fragment with a new one.
     * @param startingState The starting state of the new fragment.
     */
    public void replaceFragment(StartingState startingState) {
        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(displayedFragment.name());
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(startingState.name());

        getSupportFragmentManager().beginTransaction()
                .hide(oldFragment)
                .show(newFragment)
                .commit();

        displayedFragment = startingState;
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed.
     * @param outState The Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("fragmentPhase", displayedFragment);
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