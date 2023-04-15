package desitka.server;

import com.google.gson.Gson;

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
     * Parses all JSON files in the specified directory and returns a list of questions.
     *
     * @param dir the directory to search for JSON files
     * @return a list of questions parsed from the JSON files
     * @throws IOException if an I/O error occurs while reading the JSON files
     */
    public static List<Question> getQuestions(String dir) throws IOException {
        List<Question> questions = new ArrayList<>();
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Reader reader = new FileReader(file.toFile());
                questions.add(new Gson().fromJson(reader, Question.class));
                return FileVisitResult.CONTINUE;
            }
        });
        return questions;
    }
}
