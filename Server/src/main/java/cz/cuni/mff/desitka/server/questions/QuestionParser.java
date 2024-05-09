package cz.cuni.mff.desitka.server.questions;

import com.google.gson.Gson;
import cz.cuni.mff.desitka.JSON.Question;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for parsing question files in JSON format.
 */
public class QuestionParser {

    /**
     * Returns a list of questions parsed from JSON files in a specified directory.
     *
     * @param dir the directory containing the JSON files
     * @return a list of questions parsed from the JSON files
     * @throws RuntimeException if an I/O error occurs
     */
    public static List<Question> getQuestions(String dir) {
        List<Question> questions = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
                /**
                 * Invoked for a file in a directory.
                 *
                 * @param file the file
                 * @param attrs the file's attributes
                 * @return the visit result
                 * @throws IOException if an I/O error occurs
                 */
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Reader reader = new FileReader(file.toFile());
                    questions.add(new Gson().fromJson(reader, Question.class));
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return questions;
    }
}