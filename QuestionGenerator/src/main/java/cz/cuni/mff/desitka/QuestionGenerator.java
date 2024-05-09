package cz.cuni.mff.desitka;

import cz.cuni.mff.desitka.question.QuestionCreator;
import cz.cuni.mff.desitka.wiki.getters.CategoriesWikiGetter;
import cz.cuni.mff.desitka.wiki.getters.KeysWikiGetter;
import cz.cuni.mff.desitka.wiki.getters.ValuesWikiGetter;

/**
 * This class is responsible for generating questions.
 * It uses the WikiGetter classes to get categories, values, and keys from the wiki.
 * Then it uses the QuestionCreator class to create questions and save them.
 */
public class QuestionGenerator {
    private String startingTitle = "Kategorie:Kategorie_podle_tvůrců";
    private String categoriesDirectory = "output/categories";
    private String questionsDirectory = "output/questions";
    private int categoriesViewsLimit = 128;
    private int[] questionsViewsLimit = {128, 256, 512, 1024, 2048};

    /**
     * The main method of the class.
     * It creates a new QuestionGenerator, parses the arguments, and if successful, generates questions.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        QuestionGenerator questionGenerator = new QuestionGenerator();
        if (ArgumentParser.parseArguments(args, questionGenerator)) {
            questionGenerator.generateQuestions();
        }
    }

    /**
     * Generates questions by getting categories, values, and keys from the wiki, and then creating questions.
     */
    public void generateQuestions() {
        long startTime = System.currentTimeMillis();
        CategoriesWikiGetter.getCategories(categoriesDirectory, startingTitle);
        ValuesWikiGetter.getValues(categoriesDirectory);
        KeysWikiGetter.getKeys(categoriesDirectory, categoriesViewsLimit);
        QuestionCreator.createQuestions(categoriesDirectory, questionsDirectory, questionsViewsLimit);
        printDuration(startTime);
    }

    /**
     * Prints the duration of the question generation process.
     * @param startTime The start time of the process.
     */
    private void printDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) / 1000 + "s");
    }

    /**
     * Sets the starting title for the question generation process.
     * @param startingTitle The starting title.
     */
    public void setStartingTitle(String startingTitle) {
        this.startingTitle = startingTitle;
    }

    /**
     * Sets the directory for the categories.
     * @param categoriesDirectory The categories directory.
     */
    public void setCategoriesDirectory(String categoriesDirectory) {
        this.categoriesDirectory = categoriesDirectory;
    }

    /**
     * Sets the directory for the questions.
     * @param questionsDirectory The questions directory.
     */
    public void setQuestionsDirectory(String questionsDirectory) {
        this.questionsDirectory = questionsDirectory;
    }

    /**
     * Sets the views limit for the categories.
     * @param categoriesViewsLimit The categories views limit.
     */
    public void setCategoriesViewsLimit(int categoriesViewsLimit) {
        this.categoriesViewsLimit = categoriesViewsLimit;
    }

    /**
     * Sets the views limit for the questions.
     * @param questionsViewsLimit The questions views limit.
     */
    public void setQuestionsViewsLimit(int[] questionsViewsLimit) {
        this.questionsViewsLimit = questionsViewsLimit;
    }
}