package cz.cuni.mff.desitka.client.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.JSON.server.Waiting;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.gameLogic.GameViewModel;

/**
 * The GameStart class represents the start of a game.
 */
public class GameStart extends Fragment implements GameStates {
    private GameViewModel gameViewModel;
    private TextView joinedPlayersCount;
    private TextView gameCodeText;
    private ConstraintLayout gameCodeLayout;
    private TextView[] joinedPlayers;
    private int joiningCount;

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
        View view = inflater.inflate(R.layout.game_start, container, false);
        initialize(view);
        addObservers();
        return view;
    }

    /**
     * Initializes the view elements.
     *
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        joinedPlayersCount = view.findViewById(R.id.joined_players_count);
        gameCodeText = view.findViewById(R.id.game_code);
        gameCodeLayout = view.findViewById(R.id.game_code_layout);
        joinedPlayers = new TextView[]{
                view.findViewById(R.id.joined_player1),
                view.findViewById(R.id.joined_player2),
                view.findViewById(R.id.joined_player3),
                view.findViewById(R.id.joined_player4),
                view.findViewById(R.id.joined_player5),
        };
    }

    /**
     * Adds observers to the view elements.
     */
    private void addObservers() {
        addJoiningObserver();
        addWaitingObserver();
    }

    /**
     * Adds an observer for the joining of the game.
     */
    private void addJoiningObserver() {
        gameViewModel.getJoining().observe(getViewLifecycleOwner(), joining -> {
            joiningCount = joining.getPlayerCount();
            String gameCode = joining.getGameCode();

            gameCodeText.setText(gameCode);
            gameCodeLayout.setVisibility((gameCode == null) ? View.GONE : View.VISIBLE);
        });
    }

    /**
     * Adds an observer for the waiting of the game.
     */
    private void addWaitingObserver() {
        gameViewModel.getWaiting().observe(getViewLifecycleOwner(), waiting -> {
            List<Waiting.WaitingPlayer> waitingPlayers = waiting.getWaitingPlayers();
            String stringSource = requireContext().getResources().getString(R.string.joined_players);
            addWaitingPlayers(waiting.getWaitingPlayers());
            joinedPlayersCount.setText(String.format(stringSource, waitingPlayers.size(), joiningCount));
        });
    }

    /**
     * Adds the waiting players to the game.
     *
     * @param waitingPlayers The players who are waiting.
     */
    private void addWaitingPlayers(List<Waiting.WaitingPlayer> waitingPlayers) {
        for (int i = 0; i < joinedPlayers.length; i++) {
            if (i < waitingPlayers.size()) {
                joinedPlayers[i].setText(waitingPlayers.get(i).getPlayerName());
                joinedPlayers[i].setVisibility(View.VISIBLE);
                continue;
            }
            joinedPlayers[i].setVisibility(View.GONE);
        }
    }
}