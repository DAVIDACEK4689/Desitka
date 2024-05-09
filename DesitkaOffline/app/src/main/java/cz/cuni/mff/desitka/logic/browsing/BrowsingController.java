package cz.cuni.mff.desitka.logic.browsing;

import android.content.Context;
import java.util.List;
import cz.cuni.mff.desitka.Question;
import cz.cuni.mff.desitka.QuestionParser;
import cz.cuni.mff.desitka.interfaces.BrowsingStates;

/**
 * Controller for browsing logic.
 * This class manages the browsing functionality of the application.
 */
public class BrowsingController implements BrowsingStates {
    private final BrowsingViewModel browsingViewModel;
    private final Context context;
    private final List<Question> questions;
    private int subQuestionIndex = 0;
    private Question question;

    /**
     * Constructor for the BrowsingController class.
     * @param browsingViewModel The ViewModel for browsing logic.
     * @param context The application context.
     */
    public BrowsingController(BrowsingViewModel browsingViewModel, Context context) {
        this.browsingViewModel = browsingViewModel;
        this.context = context;
        questions = QuestionParser.getQuestions(context);
    }

    /**
     * Chooses a browsing question randomly from the list of questions.
     * If the list of questions is empty, it is refilled with questions parsed from the context.
     */
    public void chooseBrowsingQuestion() {
        if (questions.isEmpty()) {
            questions.addAll(QuestionParser.getQuestions(context));
        }
        int randomIndex = (int) (Math.random() * questions.size());
        question = questions.remove(randomIndex);
        subQuestionIndex = 0;
    }

    /**
     * Gets the next sub-question and updates the ViewModel.
     * If all sub-questions have been asked, a new question is chosen.
     */
    public void getNextSubQuestion() {
        Question.SubQuestion subQuestion = question.getSubQuestions()[subQuestionIndex++];
        browsingViewModel.getQuestionText().postValue(question.getText());
        browsingViewModel.getSubQuestion().postValue(subQuestion);

        if (subQuestionIndex == 10) {
            chooseBrowsingQuestion();
        }
    }
}