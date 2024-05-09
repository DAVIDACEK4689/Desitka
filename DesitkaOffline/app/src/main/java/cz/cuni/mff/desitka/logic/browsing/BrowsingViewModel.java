package cz.cuni.mff.desitka.logic.browsing;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cz.cuni.mff.desitka.Question;
import cz.cuni.mff.desitka.interfaces.BrowsingStates;

/**
 * ViewModel for browsing logic.
 * This class holds the data and methods related to the browsing functionality of the application.
 */
public class BrowsingViewModel extends ViewModel implements BrowsingStates {
    private BrowsingController browsingController;
    private final MutableLiveData<Question.SubQuestion> subQuestion = new MutableLiveData<>();
    private final MutableLiveData<String> questionText = new MutableLiveData<>();
    private final MutableLiveData<Integer> playerAnswer = new MutableLiveData<>();
    private final MutableLiveData<BrowsingState> displayedBrowsingFragment = new MutableLiveData<>(BrowsingState.BrowsingStart);
    private final MutableLiveData<BrowsingState> BrowsingFragmentChange = new MutableLiveData<>();
    private long sceneTime = 0;

    /**
     * Returns the current sub-question.
     * @return A MutableLiveData holding the current sub-question.
     */
    public MutableLiveData<Question.SubQuestion> getSubQuestion() {
        return subQuestion;
    }

    /**
     * Returns the text of the current question.
     * @return A MutableLiveData holding the text of the current question.
     */
    public MutableLiveData<String> getQuestionText() {
        return questionText;
    }

    /**
     * Returns the player's answer.
     * @return A MutableLiveData holding the player's answer.
     */
    public MutableLiveData<Integer> getPlayerAnswer() {
        return playerAnswer;
    }

    /**
     * Returns the currently displayed browsing fragment.
     * @return A MutableLiveData holding the currently displayed browsing fragment.
     */
    public MutableLiveData<BrowsingState> getDisplayedBrowsingFragment() {
        return displayedBrowsingFragment;
    }

    /**
     * Returns the browsing fragment change.
     * @return A MutableLiveData holding the browsing fragment change.
     */
    public MutableLiveData<BrowsingState> getBrowsingFragmentChange() {
        return BrowsingFragmentChange;
    }

    /**
     * Chooses a browsing question.
     */
    public void chooseBrowsingQuestion() {
        browsingController.chooseBrowsingQuestion();
    }

    /**
     * Gets the next sub-question.
     */
    public void getNextSubQuestion() {
        browsingController.getNextSubQuestion();
    }

    /**
     * Initializes the browsing controller.
     * @param context The application context.
     */
    public void initializeBrowsingController(Context context) {
        browsingController = new BrowsingController(this, context);
    }

    /**
     * Returns the scene time.
     * @return The scene time.
     */
    public long getSceneTime() {
        return sceneTime;
    }

    /**
     * Sets the scene time.
     * @param sceneTime The scene time to set.
     */
    public void setSceneTime(long sceneTime) {
        this.sceneTime = sceneTime;
    }
}