package cz.cuni.mff.desitka.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.logic.game.GameViewModel;

/**
 * Represents the round start fragment in the game.
 * This class is responsible for displaying the round start in the game.
 */
public class RoundStart extends Fragment {
    private GameViewModel gameViewModel;
    private TextView roundStart;

    /**
     * Called when the fragment is first created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
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
        View view = inflater.inflate(R.layout.round_start, container, false);
        initialize(view);
        addStartObserver();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        roundStart = view.findViewById(R.id.round_start);
    }

    /**
     * Adds an observer to the start of the round.
     */
    private void addStartObserver() {
        String stringSource = requireContext().getResources().getString(R.string.round_start);

        gameViewModel.getStart().observe(getViewLifecycleOwner(),
                start -> roundStart.setText(String.format(stringSource, start.getRoundNumber())));
    }
}