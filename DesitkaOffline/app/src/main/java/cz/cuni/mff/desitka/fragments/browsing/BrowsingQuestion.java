package cz.cuni.mff.desitka.fragments.browsing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.activities.StartingActivity;
import cz.cuni.mff.desitka.interfaces.BrowsingStates;
import cz.cuni.mff.desitka.logic.browsing.BrowsingViewModel;

/**
 * Represents the browsing question fragment in the game.
 * This class is responsible for displaying the browsing question in the game.
 */
public class BrowsingQuestion extends Fragment implements BrowsingStates {
    private BrowsingViewModel browsingViewModel;
    private TextView[] answers;
    private TextView questionText;
    private TextView questionDetailText;
    private Button newQuestionButton;
    private Button endBrowsingButton;

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
        View view = inflater.inflate(R.layout.browsing_question, container, false);
        initialize(view);
        addActionListeners();
        addObservers();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        questionText = view.findViewById(R.id.question_text);
        questionDetailText = view.findViewById(R.id.question_detail_text);
        newQuestionButton = view.findViewById(R.id.new_question);
        endBrowsingButton = view.findViewById(R.id.end_browsing);

        answers = new TextView[]{
                view.findViewById(R.id.answer1),
                view.findViewById(R.id.answer2),
                view.findViewById(R.id.answer3),
                view.findViewById(R.id.answer4)
        };
    }

    /**
     * Adds action listeners to the view components.
     */
    private void addActionListeners() {
        addSubmitAnswerListener();
        addNewQuestionListener();
        addEndBrowsingListener();
    }

    /**
     * Adds a listener to the submit answer button.
     */
    private void addSubmitAnswerListener() {
        for (int i = 0; i < answers.length; i++) {
            int finalI = i;
            answers[i].setOnClickListener(v -> {
                browsingViewModel.getPlayerAnswer().setValue(finalI);
                browsingViewModel.getBrowsingFragmentChange().setValue(BrowsingState.BrowsingAnswer);
            });
        }
    }

    /**
     * Adds a listener to the new question button.
     */
    private void addNewQuestionListener() {
        newQuestionButton.setOnClickListener(v -> {
            browsingViewModel.chooseBrowsingQuestion();
            browsingViewModel.getBrowsingFragmentChange().setValue(BrowsingState.BrowsingStart);
        });
    }

    /**
     * Adds a listener to the end browsing button.
     */
    private void addEndBrowsingListener() {
        endBrowsingButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), StartingActivity.class);
            requireActivity().finish();
            startActivity(intent);
        });
    }

    /**
     * Adds observers to the view components.
     */
    private void addObservers() {
        addQuestionTextObserver();
        addSubQuestionObserver();
    }

    /**
     * Adds an observer to the question text.
     */
    private void addQuestionTextObserver() {
        browsingViewModel.getQuestionText().observe(getViewLifecycleOwner(),
                text -> questionText.setText(text));
    }

    /**
     * Adds an observer to the sub-question.
     */
    private void addSubQuestionObserver() {
        browsingViewModel.getSubQuestion().observe(getViewLifecycleOwner(), subQuestion -> {
            questionDetailText.setText(subQuestion.getKey());
            String[] values = subQuestion.getValues();
            for (int i = 0; i < 4; i++) {
                answers[i].setText(values[i]);
            }
        });
    }
}