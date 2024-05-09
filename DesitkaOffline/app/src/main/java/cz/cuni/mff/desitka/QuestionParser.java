package cz.cuni.mff.desitka;

import android.content.Context;
import android.content.res.AssetManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for parsing question files in JSON format.
 */
public class QuestionParser {

    /**
     * Parses all JSON files in the "questions" directory of the application's assets and returns a list of Question objects.
     * Each file in the directory is expected to contain a single JSON object representing a Question.
     * The method uses the Gson library to parse the JSON objects.
     *
     * @param context The application context, used to access the assets.
     * @return A list of Question objects parsed from the JSON files in the "questions" directory.
     * @throws RuntimeException if an I/O error occurs while reading the JSON files or parsing the JSON objects.
     */
    public static List<Question> getQuestions(Context context) {
        List<Question> questions = new ArrayList<>();
        try {
            AssetManager assetManager = context.getAssets();
            String[] files = assetManager.list("questions");

            if (files != null) {
                for (String filename : files) {
                    InputStream inputStream = assetManager.open("questions/" + filename);
                    Reader reader = new InputStreamReader(inputStream);
                    questions.add(new Gson().fromJson(reader, Question.class));
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading question files or parsing JSON", e);
        }
        return questions;
    }
}