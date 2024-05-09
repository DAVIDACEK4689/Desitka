package cz.cuni.mff.desitka.question;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.cuni.mff.desitka.wiki.types.Article;
import cz.cuni.mff.desitka.wiki.types.Category;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides methods to create and manage questions.
 */
public class QuestionCreator {

    /**
     * Creates questions from a specified directory and copies them to another directory.
     * @param categoriesDirectory The directory to create questions from.
     * @param questionsDirectory The directory to copy the created questions to.
     * @param questionsViewsLimit The views limit for the questions.
     */
    public static void createQuestions(String categoriesDirectory, String questionsDirectory, int[] questionsViewsLimit) {
        try {
            for (int viewsLimit : questionsViewsLimit) {
                Path start = Paths.get(categoriesDirectory);

                // Remove old questions
                Files.walk(start)
                        .filter(path -> path.getFileName().toString().endsWith("questions"))
                        .forEach(QuestionCreator::deleteOldQuestions);

                // Create questions
                Files.walk(start)
                        .collect(Collectors.toList())
                        .parallelStream()
                        .filter(path -> !path.getFileName().toString().endsWith(".txt"))
                        .map(Category::new)
                        .filter(Category::isQuestionCategory)
                        .forEach(category -> createQuestions(category, viewsLimit));

                // Collect questions
                System.out.println("Questions created for views limit: " + viewsLimit);
                String destination = questionsDirectory + "_" + viewsLimit;
                QuestionCollector.collectQuestions(categoriesDirectory, destination);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes old questions from a specified path.
     * @param path The path to delete old questions from.
     */
    private static void deleteOldQuestions(Path path) {
        try {
            FileUtils.deleteDirectory(path.toFile());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates questions for a specified category and views limit.
     * @param category The category to create questions for.
     * @param viewsLimit The views limit for the questions.
     */
    private static void createQuestions(Category category, int viewsLimit) {
        try {
            Path path = category.getPath();
            String questionText = Files.lines(path.resolve("question_text.txt"))
                    .findFirst()
                    .orElse(null);

            if (questionText != null) {
                HashMap<Article, List<String>> pairs = new HashMap<>();
                LinkedHashMap<String, String> sortedPairs = new LinkedHashMap<>();
                List<Question> questions = new ArrayList<>();

                addPairs(path, pairs, viewsLimit);
                removeMultipleValues(pairs);
                sortByViews(pairs, sortedPairs);
                addQuestions(questions, sortedPairs, questionText);
                saveQuestions(questions, path);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds pairs of articles and their corresponding values to a specified map.
     * @param path The path to the articles.
     * @param pairs The map to add the pairs to.
     * @param viewsLimit The views limit for the articles.
     */
    private static void addPairs(Path path, HashMap<Article, List<String>> pairs, int viewsLimit) throws IOException {
        Files.list(path)
                .filter(value -> !value.getFileName().toString().contains("podle"))
                .filter(value -> !value.getFileName().toString().endsWith(".txt"))
                .forEach(value -> addPair(pairs, value, viewsLimit));
    }

    /**
     * Adds a pair of an article and its corresponding value to a specified map.
     * @param pairs The map to add the pair to.
     * @param path The path to the article.
     * @param viewsLimit The views limit for the article.
     */
    private static void addPair(HashMap<Article, List<String>> pairs, Path path, int viewsLimit) {
        String value = getValue(path);
        List<Article> keys = getKeys(path, viewsLimit);
        keys.forEach(key -> pairs.computeIfAbsent(key, k -> new ArrayList<>()).add(value));
    }

    /**
     * Returns the value of a specified path.
     * @param path The path to get the value from.
     * @return The value of the path.
     */
    private static String getValue(Path path) {
        try {
            return Files.list(path)
                    .filter(value -> value.getFileName().toString().equals("value.txt"))
                    .map(QuestionCreator::getText)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No value.txt found in " + path));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the text of a specified path.
     * @param path The path to get the text from.
     * @return The text of the path.
     */
    private static String getText(Path path) {
        try {
            return Files.lines(path)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No text found in " + path));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of articles from a specified path and views limit.
     * @param path The path to get the articles from.
     * @param viewsLimit The views limit for the articles.
     * @return The list of articles.
     */
    private static List<Article> getKeys(Path path, int viewsLimit) {
        try {
            return Files.walk(path)
                    .filter(value -> value.getFileName().toString().endsWith(".txt"))
                    .filter(value -> !value.getFileName().toString().equals("value.txt"))
                    .map(value -> new Article(value, Integer.parseInt(getText(value))))
                    .filter(article -> article.getViews() > viewsLimit)
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes multiple values from a specified map.
     * @param pairs The map to remove multiple values from.
     */
    private static void removeMultipleValues(HashMap<Article, List<String>> pairs) {
        pairs.entrySet().removeIf(entry -> entry.getValue().size() > 1);
    }

    /**
     * Sorts a specified map by views.
     * @param pairs The map to sort.
     * @param sortedPairs The map to store the sorted pairs in.
     */
    private static void sortByViews(HashMap<Article, List<String>> pairs, LinkedHashMap<String, String> sortedPairs) {
        pairs.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getKey().getViews(), entry1.getKey().getViews()))
                .forEach(entry -> sortedPairs.put(entry.getKey().getName().replace("_", " "), entry.getValue().get(0)));
    }

    /**
     * Adds questions to a specified list.
     * @param questions The list to add questions to.
     * @param sortedPairs The sorted pairs to create questions from.
     * @param questionText The text of the questions.
     */
    private static void addQuestions(List<Question> questions, LinkedHashMap<String, String> sortedPairs, String questionText) {
        String[] keys = sortedPairs.keySet().toArray(new String[0]);
        String[] values = sortedPairs.values().toArray(new String[0]);
        List<String> distinctValues = getDistinctValues(sortedPairs);
        int step = sortedPairs.size() / 10;

        if (!outbalancedKeys(sortedPairs) && distinctValues.size() >= 4) {
            for (int iteration = 0; iteration < step; iteration++) {
                Question.SubQuestion[] subQuestions = generateSubQuestions(keys, values, distinctValues, step, iteration);
                questions.add(new Question(questionText, subQuestions));
            }
        }
    }

    /**
     * Checks if the keys in a specified map are outbalanced.
     * @param sortedPairs The map to check.
     * @return True if the keys are outbalanced, false otherwise.
     */
    private static boolean outbalancedKeys(LinkedHashMap<String, String> sortedPairs) {
        int maxValuesCount = sortedPairs.values()
                .stream()
                .mapToInt(value -> Collections.frequency(sortedPairs.values(), value))
                .max().orElse(0);
        return maxValuesCount == 0 || maxValuesCount * 2 > sortedPairs.values().size();
    }

    /**
     * Returns a list of distinct values from a specified map.
     * @param sortedPairs The map to get the distinct values from.
     * @return The list of distinct values.
     */
    private static List<String> getDistinctValues(LinkedHashMap<String, String> sortedPairs) {
        return sortedPairs.values()
                .stream()
                .distinct().collect(Collectors.toList());
    }

    /**
     * Generates an array of sub-questions.
     * @param keys The keys of the sub-questions.
     * @param values The values of the sub-questions.
     * @param distinctValues The distinct values of the sub-questions.
     * @param step The step for the sub-questions.
     * @param startIndex The start index for the sub-questions.
     * @return The array of sub-questions.
     */
    private static Question.SubQuestion[] generateSubQuestions(String[] keys, String[] values, List<String> distinctValues, int step, int startIndex) {
        Question.SubQuestion[] subQuestions = new Question.SubQuestion[10];
        for (int j = 0; j < 10; j++) {
            List<String> randomValues = getRandomValues(distinctValues);
            int correctIndex = addValue(values[startIndex], randomValues);
            subQuestions[j] = new Question.SubQuestion(keys[startIndex], randomValues.toArray(new String[0]), correctIndex);
            startIndex += step;
        }
        return subQuestions;
    }

    /**
     * Returns a list of random values from a specified list.
     * @param distinctValues The list to get the random values from.
     * @return The list of random values.
     */
    private static List<String> getRandomValues(List<String> distinctValues) {
        Collections.shuffle(distinctValues);
        return new ArrayList<>(distinctValues.subList(0, 4));
    }

    /**
     * Adds a value to a specified list and returns its index.
     * @param value The value to add.
     * @param randomValues The list to add the value to.
     * @return The index of the added value.
     */
    private static int addValue(String value, List<String> randomValues) {
        int index = randomValues.indexOf(value);
        if (index == -1) {
            index = (int) (Math.random() * randomValues.size());
            randomValues.set(index, value);
        }
        return index;
    }

    /**
     * Saves a list of questions to a specified path.
     * @param questions The list of questions to save.
     * @param path The path to save the questions to.
     */
    private static void saveQuestions(List<Question> questions, Path path) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Path questionPath = path.resolve("questions");
            Files.createDirectories(questionPath);

            for (int i = 0; i < questions.size(); i++) {
                String index = String.format("%03d", i+1);
                Path questionFile = questionPath.resolve("question_" + index + ".json");
                String content = gson.toJson(questions.get(i));
                Files.write(questionFile, content.getBytes());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
