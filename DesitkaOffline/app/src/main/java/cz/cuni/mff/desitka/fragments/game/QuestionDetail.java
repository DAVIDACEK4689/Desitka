package cz.cuni.mff.desitka.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.logic.game.GameViewModel;
import cz.cuni.mff.desitka.interfaces.GameStates;
import cz.cuni.mff.desitka.Question;

/**
 * Represents the question detail fragment in the game.
 * This class is responsible for displaying the question detail in the game.
 */
public class QuestionDetail extends Fragment implements GameStates {
    private GameViewModel gameViewModel;
    private TextView[] answers;
    private TextView questionText;
    private Button backToQuestion;

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
        View view = inflater.inflate(R.layout.question_detail, container, false);
        initialize(view);
        addListeners();
        addQuestionDetailObserver();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        questionText = view.findViewById(R.id.question_detail_text);
        backToQuestion = view.findViewById(R.id.back_to_question);
        answers = new TextView[]{
                view.findViewById(R.id.answer1),
                view.findViewById(R.id.answer2),
                view.findViewById(R.id.answer3),
                view.findViewById(R.id.answer4)
        };
    }

    /**
     * Adds listeners to the view components.
     */
    private void addListeners() {
        addBackToQuestionListener();
        addSubmitAnswerListener();
    }

    /**
     * Adds a listener to the back to question button.
     */
    private void addBackToQuestionListener() {
        backToQuestion.setOnClickListener(v -> gameViewModel.getGameFragmentChange().setValue(GameState.PLAYER_ANSWERS));
    }

    /**
     * Adds listeners to the submit answer text views.
     */
    private void addSubmitAnswerListener() {
        for (int i = 0; i < answers.length; i++) {
            int finalI = i;
            answers[i].setOnClickListener(v -> {
                gameViewModel.processAnswer(finalI);
                gameViewModel.getGameFragmentChange().setValue(GameState.SHOW_ANSWER);
            });
        }
    }

    /**
     * Adds an observer to the question detail.
     */
    private void addQuestionDetailObserver() {
        gameViewModel.getQuestionDetailID().observe(getViewLifecycleOwner(), id -> {
            if (id != -1) {
                Question question = gameViewModel.getQuestion().getValue();
                questionText.setText(question.getSubQuestions()[id].getKey());
                String[] values = question.getSubQuestions()[id].getValues();
                for (int i = 0; i < answers.length; i++) {
                    answers[i].setText(values[i]);
                }
            }
        });
    }
}