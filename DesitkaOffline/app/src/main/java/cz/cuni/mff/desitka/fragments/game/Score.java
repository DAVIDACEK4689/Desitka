package cz.cuni.mff.desitka.fragments.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.activities.GameActivity;
import cz.cuni.mff.desitka.activities.StartingActivity;
import cz.cuni.mff.desitka.logic.game.GameViewModel;
import cz.cuni.mff.desitka.logic.game.liveData.Evaluation;

/**
 * Represents the score fragment in the game.
 * This class is responsible for displaying the score in the game.
 */
public class Score extends Fragment {
    private GameViewModel gameViewModel;
    private ConstraintLayout endGameLayout;
    private ConstraintLayout[] playerScores;
    private Button playAgain;
    private Button endGame;

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
        View view = inflater.inflate(R.layout.score, container, false);
        initialize(view);
        addListeners();
        addEvaluationObserver();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        endGameLayout = view.findViewById(R.id.end_game_layout);
        playAgain = endGameLayout.findViewById(R.id.play_again);
        endGame = endGameLayout.findViewById(R.id.end_game);

        playerScores = new ConstraintLayout[] {
                view.findViewById(R.id.player_score_layout_1),
                view.findViewById(R.id.player_score_layout_2),
                view.findViewById(R.id.player_score_layout_3),
                view.findViewById(R.id.player_score_layout_4),
                view.findViewById(R.id.player_score_layout_5)
        };

        for (ConstraintLayout playerScore : playerScores) {
            playerScore.setVisibility(View.GONE);
        }
    }

    /**
     * Adds listeners to the view components.
     */
    private void addListeners() {
        playAgain.setOnClickListener(v -> {
            String[] playerNames = gameViewModel.getPlayerNames();
            Intent intent = new Intent(requireContext(), GameActivity.class);
            intent.putExtra("playerNames", playerNames);
            requireActivity().finish();
            startActivity(intent);
        });

        endGame.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), StartingActivity.class);
            requireActivity().finish();
            startActivity(intent);
        });
    }

    /**
     * Adds an observer to the evaluation of the game.
     */
    private void addEvaluationObserver() {
        gameViewModel.getEvaluation().observe(getViewLifecycleOwner(), evaluation -> {
            setScore(evaluation.getPlayerScores());
            endGameLayout.setVisibility(evaluation.isGameOver() ? View.VISIBLE : View.GONE);
        });
    }

    /**
     * Sets the score in the view.
     * @param playerScore An array of player scores.
     */
    private void setScore(Evaluation.PlayerScore[] playerScore) {
        for (int i = 0; i < playerScore.length; i++) {
            ConstraintLayout playerScoreLayout = playerScores[i];
            TextView playerScoreName = playerScoreLayout.findViewById(R.id.player_score_name);
            TextView playerScoreValue = playerScoreLayout.findViewById(R.id.player_score_value);

            playerScoreLayout.setVisibility(View.VISIBLE);
            playerScoreName.setText(playerScore[i].getName());
            playerScoreValue.setText(String.valueOf(playerScore[i].getScore()));
        }
    }
}