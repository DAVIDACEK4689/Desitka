package cz.cuni.mff.desitka.client.fragments.starting;

import static cz.cuni.mff.desitka.JSON.client.MyJoiningRequests.RequestType.JOIN_ONLINE_GAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.client.MyJoining;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.activities.GameActivity;
import cz.cuni.mff.desitka.client.activities.StartingActivity;
import cz.cuni.mff.desitka.client.sharedPreferences.Preferences;

/**
 * The WelcomePage class represents the welcome page of the application.
 */
public class WelcomePage extends Fragment implements StartingStates {
    private Button singlePlayer;
    private Button multiPlayer;
    private Button settings;

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
        View view = inflater.inflate(R.layout.welcome_page, container, false);
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
        singlePlayer = view.findViewById(R.id.play);
        multiPlayer = view.findViewById(R.id.play_with_friends);
        settings = view.findViewById(R.id.settings);
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        singlePlayer.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GameActivity.class);
            String playerName = Preferences.getPlayerName(requireActivity());
            MyJoining myJoining = new MyJoining(JOIN_ONLINE_GAME, playerName, null, 0);

            intent.putExtra("myJoining", GsonParser.toJson(myJoining));
            requireActivity().finish();
            startActivity(intent);
        });

        multiPlayer.setOnClickListener(v ->
                ((StartingActivity) requireActivity()).replaceFragment(StartingState.FriendGamePage));

        settings.setOnClickListener(v ->
                ((StartingActivity) requireActivity()).replaceFragment(StartingState.Settings));
    }

    /**
     * Called when the fragment is no longer in use.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}