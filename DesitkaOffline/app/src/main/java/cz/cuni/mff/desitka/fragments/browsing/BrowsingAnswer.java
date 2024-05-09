package cz.cuni.mff.desitka.fragments.browsing;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.Question;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.logic.browsing.BrowsingViewModel;

/**
 * Represents the browsing answer fragment in the game.
 * This class is responsible for displaying the browsing answer in the game.
 */
public class BrowsingAnswer extends Fragment {
    private BrowsingViewModel browsingViewModel;
    private TextView questionText;
    private EditText playerAnswerText;
    private TextView correctAnswerText;

    /**
     * Called when the fragment is first created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        browsingViewModel = new ViewModelProvider(requireActivity()).get(BrowsingViewModel.class);
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
        View view = inflater.inflate(R.layout.answer, container, false);
        initialize(view);
        addObservers();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        questionText = view.findViewById(R.id.question_text_show);
        playerAnswerText = view.findViewById(R.id.player_answer);
        correctAnswerText = view.findViewById(R.id.correct_answer);
    }

    /**
     * Adds observers to the view components.
     */
    private void addObservers() {
        addPlayerAnswerObserver();
    }

    /**
     * Adds an observer to the player's answer.
     */
    private void addPlayerAnswerObserver() {
        browsingViewModel.getPlayerAnswer().observe(getViewLifecycleOwner(), playerAnswerId -> {
            Question.SubQuestion subQuestion = browsingViewModel.getSubQuestion().getValue();
            String[] values = subQuestion.getValues();
            int correctAnswerId = subQuestion.getCorrectIndex();
            boolean correct = (playerAnswerId == correctAnswerId);

            questionText.setText(subQuestion.getKey());
            playerAnswerText.setText(values[playerAnswerId]);
            correctAnswerText.setText(values[correctAnswerId]);
            correctAnswerText.setTextColor((correct) ? Color.GREEN : Color.RED);
        });
    }
}