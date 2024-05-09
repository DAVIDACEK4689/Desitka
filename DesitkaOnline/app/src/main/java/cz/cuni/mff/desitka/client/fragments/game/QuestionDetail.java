package cz.cuni.mff.desitka.client.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.client.MyAnswer;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.gameLogic.GameViewModel;

/**
 * The QuestionDetail class represents the detailed view of a question in the game.
 */
public class QuestionDetail extends Fragment implements GameStates {
    private GameViewModel gameViewModel;
    private TextView[] answers;
    private TextView questionText;
    private Button backToQuestion;

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
        View view = inflater.inflate(R.layout.question_detail, container, false);
        initialize(view);
        addListeners();
        addQuestionDetailObserver();
        return view;
    }

    /**
     * Initializes the view elements.
     *
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        questionText = view.findViewById(R.id.question_detail_text);
        backToQuestion = view.findViewById(R.id.move_back_question_detail);
        answers = new TextView[]{
                view.findViewById(R.id.answer1),
                view.findViewById(R.id.answer2),
                view.findViewById(R.id.answer3),
                view.findViewById(R.id.answer4)
        };
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        addBackToQuestionListener();
        addSubmitAnswerListener();
    }

    /**
     * Adds a listener to the back button.
     */
    private void addBackToQuestionListener() {
        backToQuestion.setOnClickListener(v -> gameViewModel.getGameStateChange().setValue(GameState.PLAYER_ANSWERS));
    }

    /**
     * Adds listeners to the answer buttons.
     */
    private void addSubmitAnswerListener() {
        for (int i = 0; i < answers.length; i++) {
            int finalI = i;
            answers[i].setOnClickListener(v -> {
                int answerID = gameViewModel.getQuestionDetailID().getValue();
                gameViewModel.sendAnswer(GsonParser.toJson(new MyAnswer(answerID, finalI)));
                gameViewModel.getGameStateChange().setValue(GameState.SHOW_ANSWER_WAITING);
            });
        }
    }

    /**
     * Adds an observer for the question detail ID.
     */
    private void addQuestionDetailObserver() {
        gameViewModel.getQuestionDetailID().observe(getViewLifecycleOwner(), id -> {
            if (id != -1) {
                cz.cuni.mff.desitka.JSON.Question question = gameViewModel.getQuestion().getValue();
                questionText.setText(question.getSubQuestions()[id].getKey());
                String[] values = question.getSubQuestions()[id].getValues();
                for (int i = 0; i < answers.length; i++) {
                    answers[i].setText(values[i]);
                }
            }
        });
    }
}