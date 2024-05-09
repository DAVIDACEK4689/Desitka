package cz.cuni.mff.desitka.client.fragments.starting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.StartingActivity;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * The SetName class represents the page where the user sets their name.
 */
public class SetName extends Fragment implements StartingStates {
    private EditText nameInput;
    private Button submitButton;

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        View view = inflater.inflate(R.layout.set_name, container, false);
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
        nameInput = view.findViewById(R.id.name_input);
        submitButton = view.findViewById(R.id.submit_settings);
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            if (name.isEmpty()) {
                StartingActivity activity = (StartingActivity) requireActivity();
                activity.showAlertDialog(R.string.empty_name);
                return;
            }

            Preferences.setPlayerName(requireContext(), name);
            StartingActivity activity = (StartingActivity) requireActivity();
            activity.updatePlayerName();
            activity.replaceFragment(StartingState.WelcomePage);
        });
    }
}