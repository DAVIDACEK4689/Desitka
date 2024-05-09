package junit_tests;

import cz.cuni.mff.desitka.QuestionGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for normal questions.
 * It uses the QuestionGenerator class to generate questions.
 */
@DisplayName("Bigger Category Test")
public class Test_03 {
    private final String startingTitle = "Kategorie:Kategorie_podle_tvůrců";
    private final String categoriesDirectory = "test_03/categories";
    private final String questionsDirectory = "test_03/questions";
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


