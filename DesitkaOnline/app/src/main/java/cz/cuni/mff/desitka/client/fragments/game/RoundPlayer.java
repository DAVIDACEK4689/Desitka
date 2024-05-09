package cz.cuni.mff.desitka.client.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.gameLogic.GameViewModel;

/**
 * The RoundPlayer class represents the player for the current round in the game.
 */
public class RoundPlayer extends Fragment {
    private GameViewModel gameViewModel;
    private TextView roundPlayer;

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
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
        View view = inflater.inflate(R.layout.round_player, container, false);
        initialize(view);
        addTurnObserver();
        return view;
    }

    /**
     * Initializes the view elements.
     *
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        roundPlayer = view.findViewById(R.id.next_player);
    }

    /**
     * Adds an observer for the turn of the game.
     */
    private void addTurnObserver() {
        String stringSource = requireContext().getResources().getString(R.string.player_on_move);
        gameViewModel.getTurn().observe(getViewLifecycleOwner(), turn ->
                roundPlayer.setText(String.format(stringSource, turn.getRoundPlayer())));
    }
}