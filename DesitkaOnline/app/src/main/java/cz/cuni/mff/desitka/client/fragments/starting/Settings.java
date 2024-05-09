package cz.cuni.mff.desitka.client.fragments.starting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.StartingActivity;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * The Settings class represents the settings page of the application.
 */
public class Settings extends Fragment implements StartingStates {
    private EditText playerName;
    private EditText serverAddress;
    private EditText serverPort;
    private Button submitButton;
    private Button restoreButton;
    private Button backButton;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        initialize(view);
        addListeners();
        return view;
    }

    /**
     * Initializes the view elements.
     *
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        // Edit texts
        playerName = view.findViewById(R.id.player_name);
        serverAddress = view.findViewById(R.id.server_address);
        serverPort = view.findViewById(R.id.server_port);

        // Buttons
        submitButton = view.findViewById(R.id.submit_settings);
        restoreButton = view.findViewById(R.id.restore_settings);
        backButton = view.findViewById(R.id.move_back_settings);

        // Set text from preferences
        playerName.setText(Preferences.getPlayerName(requireContext()));
        serverAddress.setText(Preferences.getServerAddress(requireContext()));
        serverPort.setText(String.valueOf(Preferences.getServerPort(requireContext())));
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        addSubmitButtonListener();
        addRestoreButtonListener();
        addBackButtonListener();
    }

    /**
     * Adds a listener to the submit button.
     */
    private void addSubmitButtonListener() {
        submitButton.setOnClickListener(v -> {
            String name = playerName.getText().toString();
            String address = serverAddress.getText().toString();
            String port = serverPort.getText().toString();
            StartingActivity activity = (StartingActivity) requireActivity();

            // Validate fields
            if (name.isEmpty() || address.isEmpty() || port.isEmpty()) {
                activity.showAlertDialog(R.string.empty_settings);
                return;
            }
            else if (!port.matches("\\d+")) {
                activity.showAlertDialog(R.string.invalid_port);
                return;
            }

            // Save settings
            Preferences.setPlayerName(requireContext(), name);
            Preferences.setServerAddress(requireContext(), address);
            Preferences.setServerPort(requireContext(), Integer.parseInt(port));
            activity.replaceFragment(StartingState.WelcomePage);
        });
    }

    /**
     * Adds a listener to the restore button.
     */
    private void addRestoreButtonListener() {
        restoreButton.setOnClickListener(v -> {
            serverAddress.setText(Constants.getServerURL());
            serverPort.setText(String.valueOf(Constants.getServerPort()));
        });
    }

    /**
     * Adds a listener to the back button.
     */
    private void addBackButtonListener() {
        backButton.setOnClickListener(v -> {
            StartingActivity activity = (StartingActivity) requireActivity();
            activity.replaceFragment(StartingState.WelcomePage);
        });
    }
}