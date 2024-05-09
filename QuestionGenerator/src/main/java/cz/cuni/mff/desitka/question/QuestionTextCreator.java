package cz.cuni.mff.desitka.question;


import cz.cuni.mff.desitka.dictionaries.Dictionary;
import cz.cuni.mff.desitka.wiki.types.Category;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to create text for questions.
 */
public class QuestionTextCreator {
    private static final String[] NORMAL_TEXT = {"Urcete", "Doplnte", "Poznejte"};
    private static final String[] ADJECTIVE_TEXT = {"Jedna se o", "Jde o"};

    /**
     * Creates a question text for a specified category.
     * @param category The category to create the question text for.
     * @param hasAdjectiveValues A boolean indicating whether the category has adjective values.
     * @return True if the question text was created successfully, false otherwise.
     */
    public static boolean createQuestionText(Category category, boolean hasAdjectiveValues) {
        String questionText = hasAdjectiveValues ? createAdjectiveQuestionText(category)
                                                 : createNormalQuestionText(category);

        if (questionText != null) {
            saveQuestionText(category.getPath(), questionText);
            return true;
        }
        return false;
    }

    /**
     * Saves a question text to a specified path.
     * @param path The path to save the question text to.
     * @param questionText The question text to save.
     */
    private static void saveQuestionText(Path path, String questionText) {
        try {
            // Save the question text to the file
            Files.write(path.resolve("question_text.txt"), questionText.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an adjective question text for a specified category.
     * @param category The category to create the adjective question text for.
     * @return The adjective question text if created successfully, null otherwise.
     */
    private static String createAdjectiveQuestionText(Category category) {
        String[] categoryWords = category.getName().split("_");
        int categoryLength = categoryWords.length;

        if (categoryLength != 5) {
            List<String> result = new ArrayList<>();
            result.add(getRandomItem(ADJECTIVE_TEXT));
            result.add("[" + Dictionary.getNoun(categoryWords[categoryLength - 1], 1, 2) + "]");
            result.add(categoryLength == 3 ? Dictionary.getNoun(categoryWords[0], 4, 1)
                                           : Dictionary.getCompound(categoryWords[0], categoryWords[1], 4, 1));
            return result.contains(null) ? null : (String.join(" ", result) + "?");
        }
        return null;
    }

    /**
     * Creates a normal question text for a specified category.
     * @param category The category to create the normal question text for.
     * @return The normal question text if created successfully, null otherwise.
     */
    private static String createNormalQuestionText(Category category) {
        String[] questionWords = category.getName().split("_podle_");
        String[] startingText = questionWords[1].split("_");
        String[] endingText = questionWords[0].split("_");

        List<String> result = new ArrayList<>();
        result.add(getRandomItem(NORMAL_TEXT));
        result.add(startingText.length == 1 ? Dictionary.getNoun(startingText[0], 4, 2)
                                            : Dictionary.getCompound(startingText[0], startingText[1], 4, 2));
        result.add("podle");
        result.add(endingText.length == 1 ? Dictionary.getNoun(endingText[0], 2, 1)
                                          : Dictionary.getCompound(endingText[0], endingText[1], 2, 1));

        return result.contains(null) ? null : String.join(" ", result);
    }

    /**
     * Returns a random item from a specified array.
     * @param array The array to get the random item from.
     * @return The random item from the array.
     */
    private static String getRandomItem(String[] array) {
        int length = array.length;
        int randomIndex = (int) (Math.random() * length);
        return array[randomIndex];
    }
}
