package junit_tests;

import cz.cuni.mff.desitka.QuestionGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for normal questions.
 * It uses the QuestionGenerator class to generate questions.
 */
@DisplayName("Adjective Question Test")
public class Test_02 {
    private final String startingTitle = "Kategorie:Romány_podle_zemí";
    private final String categoriesDirectory = "test_02/categories";
    private final String questionsDirectory = "test_02/questions";
    private final int categoriesViewsLimit = 128;
    private final int[] questionsViewsLimit = {128, 256, 512, 1024, 2048};

    /**
     * This test method generates questions using the QuestionGenerator class.
     * It sets the necessary values for the QuestionGenerator and then calls the generateQuestions method.
     */
    @Test
    void test() {
        QuestionGenerator questionGenerator = new QuestionGenerator();

        // set values
        questionGenerator.setStartingTitle(startingTitle);
        questionGenerator.setCategoriesDirectory(categoriesDirectory);
        questionGenerator.setQuestionsDirectory(questionsDirectory);
        questionGenerator.setCategoriesViewsLimit(categoriesViewsLimit);
        questionGenerator.setQuestionsViewsLimit(questionsViewsLimit);

        // generate questions
        questionGenerator.generateQuestions();
    }
}
