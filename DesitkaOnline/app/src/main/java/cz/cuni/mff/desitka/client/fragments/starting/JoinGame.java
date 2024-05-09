package cz.cuni.mff.desitka.client.fragments.starting;

import static cz.cuni.mff.desitka.JSON.client.MyJoiningRequests.RequestType.JOIN_FRIEND_GAME;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.client.MyJoining;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.GameActivity;
import cz.cuni.mff.desitka.client.activities.StartingActivity;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * The JoinGame class represents the page where the user joins a game.
 */
public class JoinGame extends Fragment implements StartingStates {
    private EditText gameCode;
    private Button confirmCode;
    private Button moveBack;
    String gameCodeText;

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCodeText = (savedInstanceState == null) ? "" : savedInstanceState.getString("gameCode");
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
        View view = inflater.inflate(R.layout.join_game_page, container, false);
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
        gameCode = view.findViewById(R.id.game_code);
        confirmCode = view.findViewById(R.id.confirm_code);
        moveBack = view.findViewById(R.id.move_back_join_game);

        gameCode.setText(gameCodeText);
        confirmCode.setEnabled(gameCodeText.length() == 8);
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        addGameCodeListener();
        addConfirmCodeListener();
        addMoveBackListener();
    }

    /**
     * Adds a listener to the game code.
     */
    private void addGameCodeListener() {
        gameCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) {
                confirmCode.setEnabled(s.length() == 8);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    /**
     * Adds a listener to the confirm code button.
     */
    private void addConfirmCodeListener() {
        confirmCode.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), GameActivity.class);
            String playerName = Preferences.getPlayerName(requireContext());
            String code = gameCode.getText().toString().trim();
            MyJoining myJoining = new MyJoining(JOIN_FRIEND_GAME, playerName, code, 0);

            intent.putExtra("myJoining", GsonParser.toJson(myJoining));
            requireActivity().finish();
            startActivity(intent);
        });
    }

    /**
     * Adds a listener to the move back button.
     */
    private void addMoveBackListener() {
        moveBack.setOnClickListener(view ->
                ((StartingActivity) requireActivity()).replaceFragment(StartingState.FriendGamePage));
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be reconstructed in a new instance.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("gameCode", gameCode.getText().toString().trim());
        super.onSaveInstanceState(outState);
    }
}