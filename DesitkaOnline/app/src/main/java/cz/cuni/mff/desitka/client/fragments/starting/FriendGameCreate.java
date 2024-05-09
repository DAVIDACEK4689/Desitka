package cz.cuni.mff.desitka.client.fragments.starting;

import static cz.cuni.mff.desitka.JSON.client.MyJoiningRequests.RequestType.CREATE_FRIEND_GAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;

import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.client.MyJoining;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.GameActivity;
import cz.cuni.mff.desitka.client.activities.StartingActivity;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * The FriendGameCreate class represents the page where the user can set up a game.
 */
public class FriendGameCreate extends Fragment implements StartingStates {
    private TextView[] playerCounts;
    private Button createGame;
    private Button moveBack;
    private int playerCountIndex;

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerCountIndex = (savedInstanceState == null) ? Preferences.getFriendGamePlayers(requireContext())
                                                        : savedInstanceState.getInt("playerCountIndex");
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
        View view = inflater.inflate(R.layout.friend_game_create, container, false);
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
        playerCounts = new TextView[]{view.findViewById(R.id.player_count1),
                                      view.findViewById(R.id.player_count2),
                                      view.findViewById(R.id.player_count3),
                                      view.findViewById(R.id.player_count4)};
        createGame = view.findViewById(R.id.start_game);
        moveBack = view.findViewById(R.id.move_back_friend_game_create);

        int color = ContextCompat.getColor(requireContext(), R.color.button_background);
        playerCounts[playerCountIndex].setBackgroundColor(color);
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        addPlayerCountListeners();
        addCreateGameListener();
        addMoveBackListener();
    }

    /**
     * Adds listeners to the player count buttons.
     */
    private void addPlayerCountListeners() {
        int neutralColor = ContextCompat.getColor(requireContext(), R.color.card_background);
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.button_background);

        for (TextView textView : playerCounts) {
            textView.setOnClickListener(v -> {
                playerCounts[playerCountIndex].setBackgroundColor(neutralColor);
                textView.setBackgroundColor(selectedColor);
                playerCountIndex = Arrays.asList(playerCounts).indexOf(textView);
            });
        }
    }

    /**
     * Adds a listener to the create game button.
     */
    private void addCreateGameListener() {
        createGame.setOnClickListener(v -> {
            Preferences.setFriendGamePlayers(requireContext(), playerCountIndex);
            Intent intent = new Intent(requireContext(), GameActivity.class);
            String name = Preferences.getPlayerName(requireActivity());
            MyJoining myJoining = new MyJoining(CREATE_FRIEND_GAME, name, null, playerCountIndex + 2);

            intent.putExtra("myJoining", GsonParser.toJson(myJoining));
            requireActivity().finish();
            startActivity(intent);
        });
    }

    /**
     * Adds a listener to the move back button.
     */
    private void addMoveBackListener() {
        moveBack.setOnClickListener(v ->
                ((StartingActivity) requireActivity()).replaceFragment(StartingState.FriendGamePage));
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be reconstructed in a new instance.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("playerCountIndex", playerCountIndex);
    }
}