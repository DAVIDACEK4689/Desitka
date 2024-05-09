package cz.cuni.mff.desitka.client.fragments.starting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.StartingActivity;

/**
 * The FriendGamePage class represents the page where the user can choose to set up or join a game.
 */
public class FriendGamePage extends Fragment implements StartingStates {
    private Button setupGame;
    private Button joinGame;
    private Button moveBack;

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
        View view = inflater.inflate(R.layout.friend_game, container, false);
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
        setupGame = view.findViewById(R.id.create_game);
        joinGame = view.findViewById(R.id.join_game);
        moveBack = view.findViewById(R.id.move_back_friend_game_page);
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        StartingActivity activity = (StartingActivity) requireActivity();
        setupGame.setOnClickListener(v -> activity.replaceFragment(StartingState.FriendGameCreate));
        joinGame.setOnClickListener(v -> activity.replaceFragment(StartingState.JoinGame));
        moveBack.setOnClickListener(v -> activity.replaceFragment(StartingState.WelcomePage));
    }
}