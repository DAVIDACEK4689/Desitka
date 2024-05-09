package cz.cuni.mff.desitka.question;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.nio.file.Files.createDirectory;

/**
 * This class provides methods to collect and manage questions.
 */
public class QuestionCollector {

    /**
     * Collects questions from a specified directory and copies them to another directory.
     * @param categoriesDirectory The directory to collect questions from.
     * @param questionsDirectory The directory to copy the collected questions to.
     */
    static void collectQuestions(String categoriesDirectory, String questionsDirectory) {
        try {
            // clean final directory and collect questions
            List<Path> questionsList = new CopyOnWriteArrayList<>();
            cleanQuestionsDirectory(questionsDirectory);
            Files.walk(Paths.get(categoriesDirectory))
                    .collect(Collectors.toList())
                    .parallelStream()
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .forEach(questionsList::add);

            // copy questions to the final directory
            copyQuestions(questionsDirectory, questionsList);
            printSummary(questionsDirectory, questionsList);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints a summary of the collected questions.
     * @param questionsDirectory The directory where the questions were copied to.
     * @param questionsList The list of collected questions.
     */
    private static void printSummary(String questionsDirectory, List<Path> questionsList) {
        System.out.println("Questions collected to: " + questionsDirectory);
        System.out.println("Total questions count: " + questionsList.size());
        System.out.println("Unique questions count: " + getUniqueQuestionsCount(questionsList));
        System.out.println();
    }

    /**
     * Returns the count of unique questions in a list of questions.
     * @param questionsList The list of questions.
     * @return The count of unique questions.
     */
    private static long getUniqueQuestionsCount(List<Path> questionsList) {
        return questionsList.stream().filter(path -> path.getFileName().toString().endsWith("001.json")).count();
    }

    /**
     * Copies questions from a list to a specified directory.
     * @param questionsDirectory The directory to copy the questions to.
     * @param questionsList The list of questions to copy.
     */
    private static void copyQuestions(String questionsDirectory, List<Path> questionsList) {
            try {
                for (int i = 0; i < questionsList.size(); i++) {
                    String index = String.format("%04d", i+1);
                    String fileName = "question_" + index + ".json";
                    Files.copy(questionsList.get(i), Paths.get(questionsDirectory + "/" + fileName));
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Cleans a specified questions directory.
     * @param questionsDirectory The directory to clean.
     */
    protected static void cleanQuestionsDirectory(String questionsDirectory) {
        try {
            File questionDirectory = new File(questionsDirectory);
            FileUtils.deleteDirectory(questionDirectory);
            FileUtils.createParentDirectories(questionDirectory);
            createDirectory(Paths.get(questionsDirectory));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
