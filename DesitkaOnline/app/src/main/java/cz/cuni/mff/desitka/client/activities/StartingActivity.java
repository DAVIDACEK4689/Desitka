package cz.cuni.mff.desitka.client.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.fragments.starting.Settings;
import cz.cuni.mff.desitka.client.fragments.starting.StartingStates;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * The StartingActivity class represents the starting activity of the application.
 */
public class StartingActivity extends AppCompatActivity implements StartingStates {
    private StartingState displayedFragment;
    private int alertID;
    private OnBackPressedCallback callback;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-constructed from a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_activity);
        loadState(savedInstanceState);
        registerBackPressedCallback();
        handleScreenOrientation();
        showAlertDialog(alertID);
    }

    /**
     * Registers a callback to be invoked when the back button is pressed.
     */
    private void registerBackPressedCallback() {
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                switch (displayedFragment) {
                    case FriendGamePage:
                        replaceFragment(StartingState.WelcomePage);
                        break;
                    case FriendGameCreate:
                        replaceFragment(StartingState.FriendGamePage);
                        break;
                    case JoinGame:
                        replaceFragment(StartingState.FriendGamePage);
                        break;
                    case Settings:
                        replaceFragment(StartingState.WelcomePage);
                        break;
                    default:
                        break;
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
     * Checks if it's the first start of the application.
     *
     * @return true if it's the first start, false otherwise.
     */
    private boolean firstStart() {
        String playerName = Preferences.getPlayerName(this);
        return playerName.isEmpty();
    }

    /**
     * Loads the state of the activity.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    private void loadState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            alertID = savedInstanceState.getInt("alertID");
            displayedFragment = (StartingState) savedInstanceState.getSerializable("fragmentPhase");
            return;
        }
        displayedFragment = firstStart() ? StartingState.SetName : StartingState.WelcomePage;
        alertID = -1;
        initializeFragments();
        startFragment();
    }

    /**
     * Initializes the fragments of the activity.
     */
    private void initializeFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (StartingState startingState : StartingState.values()) {
            try {
                String fragmentTag = startingState.name();
                String fragmentName = "cz.cuni.mff.desitka.client.fragments.starting." + fragmentTag;
                Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);

                Fragment fragment = fragmentClass.newInstance();
                transaction.add(R.id.starting_frame, fragment, fragmentTag)
                           .hide(fragment);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        transaction.commitNow();
    }

    /**
     * Starts the fragment.
     */
    private void startFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(displayedFragment.name());
        getSupportFragmentManager().beginTransaction()
                .show(Objects.requireNonNull(fragment))
                .commitNow();
    }

    /**
     * Replaces the current fragment with a new one.
     *
     * @param startingState The state of the new fragment.
     */
    public void replaceFragment(StartingState startingState) {
        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(displayedFragment.name());
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(startingState.name());

        getSupportFragmentManager().beginTransaction()
                .hide(Objects.requireNonNull(oldFragment))
                .show(Objects.requireNonNull(newFragment))
                .commit();

        displayedFragment = startingState;
    }

    /**
     * Shows an alert dialog with a specific error message.
     *
     * @param errorID The ID of the error message.
     */
    public void showAlertDialog(int errorID) {
        if (errorID != -1) {
            alertID = errorID;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage(errorID)
                    .setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            button.setTextColor(ContextCompat.getColor(this, R.color.primaryColor));
            button.setOnClickListener(v -> {
                alertID = -1;
                alertDialog.dismiss();
            });
        }
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle).
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("fragmentPhase", displayedFragment);
        outState.putInt("alertID", alertID);
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

    /**
     * Updates the player name in the settings fragment.
     */
    public void updatePlayerName() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(StartingState.Settings.name());
        ((Settings) fragment).updatePlayerName();
    }
}