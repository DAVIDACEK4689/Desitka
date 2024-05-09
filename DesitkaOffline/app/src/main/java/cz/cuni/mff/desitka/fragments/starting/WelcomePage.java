package cz.cuni.mff.desitka.fragments.starting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.activities.BrowsingActivity;
import cz.cuni.mff.desitka.activities.StartingActivity;
import cz.cuni.mff.desitka.interfaces.StartingStates;

/**
 * Represents the welcome page of the application.
 * This class is responsible for handling user interactions on the welcome page.
 */
public class WelcomePage extends Fragment implements StartingStates {
    private Button singlePlayer;
    private Button multiPlayer;

    /**
     * Called when the fragment is first created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_page, container, false);
        initialize(view);
        addListeners();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        singlePlayer = view.findViewById(R.id.single_player);
        multiPlayer = view.findViewById(R.id.multi_player);
    }

    /**
     * Adds listeners to the view components.
     */
    private void addListeners() {
        singlePlayer.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BrowsingActivity.class);
            requireActivity().finish();
            startActivity(intent);
        });

        multiPlayer.setOnClickListener(v -> ((StartingActivity) requireActivity())
                .replaceFragment(StartingState.FriendGamePage));
    }

    /**
     * Called when the Fragment is no longer resumed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}