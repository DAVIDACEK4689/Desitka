package cz.cuni.mff.desitka.fragments.game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.logic.game.GameViewModel;
import cz.cuni.mff.desitka.Question;

/**
 * Represents the answer fragment in the game.
 * This class is responsible for displaying the answer in the game.
 */
public class Answer extends Fragment {
    private GameViewModel gameViewModel;
    private ConstraintLayout playerAnswerShow;
    private TextView playerNoAnswer;
    private TextView questionText;
    private EditText playerAnswerText;
    private TextView correctAnswerText;
    private TextView answerVerdictText;
    private String playerOnMove;

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
        playerAnswerShow = view.findViewById(R.id.player_answer_show);
        playerNoAnswer = view.findViewById(R.id.player_no_answer);

        questionText = view.findViewById(R.id.question_text_show);
        playerAnswerText = view.findViewById(R.id.player_answer);
        correctAnswerText = view.findViewById(R.id.correct_answer);
        answerVerdictText = view.findViewById(R.id.answer_verdict);
    }

    /**
     * Adds observers to the view components.
     */
    private void addObservers() {
        addAnswerObserver();
    }

    /**
     * Adds an observer to the answer.
     */
    private void addAnswerObserver() {
        gameViewModel.getAnswer().observe(getViewLifecycleOwner(), answer -> {
            playerOnMove = answer.getPlayerName();
            int answerID = answer.getAnswerID();
            if (answerID == -1) {
                playerPassed();
                return;
            }
            playerAnswered(answer);
        });
    }

    /**
     * Handles the case when a player passes.
     */
    private void playerPassed() {
        playerNoAnswer.setVisibility(View.VISIBLE);
        playerAnswerShow.setVisibility(View.GONE);
        answerVerdictText.setVisibility(View.GONE);
        String playerPassesSource = requireContext().getResources().getString(R.string.player_passes);
        playerNoAnswer.setText(String.format(playerPassesSource, playerOnMove));
    }

    /**
     * Handles the case when a player answers.
     * @param answer The answer provided by the player.
     */
    private void playerAnswered(cz.cuni.mff.desitka.logic.game.liveData.Answer answer) {
        playerNoAnswer.setVisibility(View.GONE);
        playerAnswerShow.setVisibility(View.VISIBLE);
        answerVerdictText.setVisibility(View.VISIBLE);
        String correctAnswerSource = requireContext().getResources().getString(R.string.correct_answer);
        String wrongAnswerSource = requireContext().getResources().getString(R.string.wrong_answer);

        int answerId = answer.getAnswerID();
        int playerAnswerIndex = answer.getPlayerAnswerIndex();
        int correctAnswerIndex = answer.getCorrectAnswerIndex();
        boolean correct = (playerAnswerIndex == correctAnswerIndex);

        Question question = gameViewModel.getQuestion().getValue();
        Question.SubQuestion subQuestion = question.getSubQuestions()[answerId];
        String[] values = subQuestion.getValues();

        questionText.setText(subQuestion.getKey());
        playerAnswerText.setText(values[playerAnswerIndex]);
        correctAnswerText.setText(values[correctAnswerIndex]);
        correctAnswerText.setTextColor((correct) ? Color.GREEN : Color.RED);
        answerVerdictText.setText((correct) ? String.format(correctAnswerSource, playerOnMove)
                                            : String.format(wrongAnswerSource, playerOnMove));
    }
}