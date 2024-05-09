package cz.cuni.mff.desitka.fragments.game;

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
import cz.cuni.mff.desitka.logic.game.GameViewModel;
import cz.cuni.mff.desitka.interfaces.GameStates;
import cz.cuni.mff.desitka.logic.game.liveData.Turn;

/**
 * Represents the question fragment in the game.
 * This class is responsible for displaying the question in the game.
 */
public class Question extends Fragment implements GameStates {
    private GameViewModel gameViewModel;
    private ConstraintLayout overviewLayout;
    private TextView questionText;
    private Button passButton;
    private TextView playerScore;
    private TextView playersInRound;
    private ConstraintLayout[] questions;

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
        View view = inflater.inflate(R.layout.question, container, false);
        initialize(view);
        addListeners();
        addObservers();
        return view;
    }

    /**
     * Adds listeners to the view components.
     */
    private void addListeners() {
        addPassButtonListener();
        addQuestionListener();
    }

    /**
     * Adds observers to the view components.
     */
    private void addObservers() {
        addQuestionObserver();
        addTurnObserver();
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        questionText = view.findViewById(R.id.question_text);
        overviewLayout = view.findViewById(R.id.overview_layout);

        passButton = overviewLayout.findViewById(R.id.pass_button);
        playerScore = overviewLayout.findViewById(R.id.player_score);
        playersInRound = overviewLayout.findViewById(R.id.players_in_round);

        questions = new ConstraintLayout[] {
                view.findViewById(R.id.question_1),
                view.findViewById(R.id.question_2),
                view.findViewById(R.id.question_3),
                view.findViewById(R.id.question_4),
                view.findViewById(R.id.question_5),
                view.findViewById(R.id.question_6),
                view.findViewById(R.id.question_7),
                view.findViewById(R.id.question_8),
                view.findViewById(R.id.question_9),
                view.findViewById(R.id.question_10)
        };
    }

    /**
     * Adds a listener to the pass button.
     */
    private void addPassButtonListener() {
        passButton.setOnClickListener(v -> {
            gameViewModel.getQuestionDetailID().setValue(-1);
            gameViewModel.processAnswer(-1);
            gameViewModel.getGameFragmentChange().setValue(GameState.SHOW_ANSWER);
        });
    }

    /**
     * Adds listeners to the question text views.
     */
    private void addQuestionListener() {
        for (int i = 0; i < questions.length; i++) {
            int finalI = i;
            TextView answerText = questions[i].findViewById(R.id.answer);
            answerText.setOnClickListener(v -> {
                if (answerText.getText().equals("?")) {
                    gameViewModel.getQuestionDetailID().setValue(finalI);
                    gameViewModel.getGameFragmentChange().setValue(GameState.PLAYER_ANSWERS_DETAIL);
                }
            });
        }
    }

    /**
     * Adds an observer to the turn.
     */
    private void addTurnObserver() {
        gameViewModel.getTurn().observe(getViewLifecycleOwner(), this::updateUIComponents);
    }

    /**
     * Adds an observer to the question.
     */
    private void addQuestionObserver() {
        gameViewModel.getQuestion().observe(getViewLifecycleOwner(), question -> {
            questionText.setText(question.getText());
            setQuestions(question);
        });
    }

    /**
     * Updates the UI components based on the turn.
     * @param turn The current turn.
     */
    private void updateUIComponents(Turn turn) {
        String roundStringSource = requireContext().getResources().getString(R.string.players_in_round);
        playerScore.setText(String.valueOf(turn.getPlayerRoundScore()));
        playersInRound.setText(String.format(roundStringSource, turn.getRoundPlayers(), turn.getGamePlayers()));
        overviewLayout.setVisibility((turn.getRoundPlayer() == null) ? View.GONE : View.VISIBLE);
    }

    /**
     * Sets the questions in the view.
     * @param question The question to set.
     */
    private void setQuestions(cz.cuni.mff.desitka.Question question) {
        cz.cuni.mff.desitka.Question.SubQuestion[] subQuestions = question.getSubQuestions();
        for (int i = 0; i < 10; i++) {
            TextView questionText = questions[i].findViewById(R.id.question);
            TextView answerText = questions[i].findViewById(R.id.answer);
            questionText.setText(subQuestions[i].getKey());

            int correctIndex = subQuestions[i].getCorrectIndex();
            if (correctIndex != -1) {
                String[] answerValues = subQuestions[i].getValues();
                answerText.setText(answerValues[correctIndex]);
                continue;
            }
            answerText.setText(requireContext().getResources().getString(R.string.question_mark));
        }
    }
}