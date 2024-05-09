package cz.cuni.mff.desitka.client.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.JSON.Constants;
import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.JSON.GsonParser;
import cz.cuni.mff.desitka.JSON.client.MyAnswer;
import cz.cuni.mff.desitka.JSON.server.helper.Turn;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.client.gameLogic.GameViewModel;

/**
 * The Question class represents the question page in the game.
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
        View view = inflater.inflate(R.layout.question, container, false);
        initialize(view);
        addListeners();
        addObservers();
        return view;
    }

    /**
     * Adds listeners to the view elements.
     */
    private void addListeners() {
        addPassButtonListener();
        addQuestionListener();
    }

    /**
     * Adds observers to the view elements.
     */
    private void addObservers() {
        addQuestionObserver();
        addTurnObserver();
    }

    /**
     * Initializes the view elements.
     *
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
            gameViewModel.sendAnswer(GsonParser.toJson(new MyAnswer(Constants.PLAYER_PASSES, -1)));
            gameViewModel.getGameStateChange().setValue(GameState.SHOW_ANSWER_WAITING);
        });
    }

    /**
     * Adds listeners to the question elements.
     */
    private void addQuestionListener() {
        for (int i = 0; i < questions.length; i++) {
            int finalI = i;
            TextView answerText = questions[i].findViewById(R.id.answer);
            answerText.setOnClickListener(v -> {
                boolean myTurn = gameViewModel.getTurn().getValue().isMyTurn();
                if (answerText.getText().equals("?") && myTurn) {
                    gameViewModel.getQuestionDetailID().setValue(finalI);
                    gameViewModel.getGameStateChange().setValue(GameState.PLAYER_ANSWERS_DETAIL);
                }
            });
        }
    }

    /**
     * Adds an observer for the turn of the game.
     */
    private void addTurnObserver() {
        gameViewModel.getTurn().observe(getViewLifecycleOwner(), this::updateUIComponents);
    }

    /**
     * Adds an observer for the question of the game.
     */
    private void addQuestionObserver() {
        gameViewModel.getQuestion().observe(getViewLifecycleOwner(), question -> {
            questionText.setText(question.getText());
            setQuestions(question);
        });
    }

    /**
     * Updates the UI components based on the turn of the game.
     *
     * @param turn The turn of the game.
     */
    private void updateUIComponents(Turn turn) {
        String roundStringSource = requireContext().getResources().getString(R.string.players_in_round);
        passButton.setEnabled(turn.isMyTurn());
        playerScore.setText(String.valueOf(turn.getMyScore()));
        playersInRound.setText(String.format(roundStringSource, turn.getRoundPlayers(), turn.getGamePlayers()));
        overviewLayout.setVisibility((turn.getRoundPlayer() == null) ? View.GONE : View.VISIBLE);
    }

    /**
     * Sets the questions for the game.
     *
     * @param question The question of the game.
     */
    private void setQuestions(cz.cuni.mff.desitka.JSON.Question question) {
        cz.cuni.mff.desitka.JSON.Question.SubQuestion[] subQuestions = question.getSubQuestions();
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