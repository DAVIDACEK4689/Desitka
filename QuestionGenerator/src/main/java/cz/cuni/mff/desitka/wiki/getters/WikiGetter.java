package cz.cuni.mff.desitka.wiki.getters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import cz.cuni.mff.desitka.JsoupGetter;
import cz.cuni.mff.desitka.wiki.types.Article;
import cz.cuni.mff.desitka.wiki.types.Category;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.nio.file.Files.createDirectory;

/**
 * This abstract class provides methods for getting information from a wiki.
 * It includes methods for getting child categories, child articles, parent categories, and the main article of a category.
 * It also includes methods for cleaning the starting directory and checking if a category has an article.
 */
public abstract class WikiGetter {
    private static final String childrenURL = "https://cs.wikipedia.org/w/api.php?action=query&list=categorymembers&format=json&cmlimit=max&cmtitle=";
    private static final String parentsURL = "https://cs.wikipedia.org/w/api.php?action=query&prop=categories&format=json&titles=";
    private static final String mainArticleURL = "https://cs.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=";
    private static final ConcurrentHashMap<String, Document> wikiCache = new ConcurrentHashMap<>();

    /**
     * Cleans the starting directory by deleting it and creating a new one.
     * @param directory The directory to clean.
     */
    protected static void cleanStartingDirectory(String directory) {
        try {
            File startingDirectory = new File(directory);
            FileUtils.deleteDirectory(startingDirectory);
            FileUtils.createParentDirectories(startingDirectory);
            createDirectory(Paths.get(directory));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the child categories of a specified category.
     * @param category The category to get the child categories of.
     * @return A list of the child categories.
     */
    @NotNull
    protected static List<Category> getChildCategories(Category category) {
    JsonObject jsonObject = parseJson(category, childrenURL);
        if (jsonObject != null) {
            JsonArray childCategories = jsonObject.getAsJsonObject("query").getAsJsonArray("categorymembers");
            return childCategories.asList()
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .map(item -> item.get("title").getAsString())
                    .filter(WikiGetter::validChars)
                    .filter(title -> title.startsWith("Kategorie:"))
                    .map(title -> new Category(title, category.getPath()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Gets the child articles of a specified category.
     * @param category The category to get the child articles of.
     * @return A list of the child articles.
     */
    @NotNull
    protected static List<Article> getChildArticles(Category category) {
        JsonObject jsonObject = parseJson(category, childrenURL);
        if (jsonObject != null) {
            JsonArray childCategories = jsonObject.getAsJsonObject("query").getAsJsonArray("categorymembers");
            return childCategories.asList()
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .map(item -> item.get("title").getAsString())
                    .filter(WikiGetter::validChars)
                    .filter(title -> !title.startsWith("Kategorie:"))
                    .map(title -> new Article(title, category.getPath()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Gets the parent categories of a specified category.
     * @param category The category to get the parent categories of.
     * @return A list of the parent categories.
     */
    @NotNull
    protected static List<Category> getParentCategories(Category category) {
        JsonObject jsonObject = parseJson(category, parentsURL);
        if (jsonObject != null) {
            // get first page
            JsonObject pages = jsonObject.getAsJsonObject("query").getAsJsonObject("pages");
            String firstPageId = pages.keySet().iterator().next();
            JsonObject firstPage = pages.getAsJsonObject(firstPageId);
            JsonArray parentCategories = firstPage.getAsJsonArray("categories");

            return parentCategories.asList()
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .map(item -> item.get("title").getAsString())
                    .filter(WikiGetter::validChars)
                    .map(title -> new Category(title, category.getPath()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Gets the starting letters of a specified name.
     * @param name The name to get the starting letters of.
     * @return An array of the starting letters.
     */
    protected static String[] getStartingLetters(String name) {
        return Arrays.stream(name.split("_"))
                .filter(word -> word.length() >= 3)
                .map(word -> word.substring(0, 3))
                .toArray(String[]::new);
    }

    /**
     * Checks if a specified category has an article.
     * @param category The category to check.
     * @return True if the category has an article, false otherwise.
     */
    protected static boolean categoryHasArticle(Category category) {
        return parseJson(category, parentsURL) != null;
    }

    /**
     * Checks if a specified category does not have an article.
     * @param category The category to check.
     * @return True if the category does not have an article, false otherwise.
     */
    protected static boolean categoryHasNotArticle(Category category) {
        return !categoryHasArticle(category);
    }

    /**
     * Checks if the title is valid.
     * A title is considered valid if it contains only letters, numbers, diacritical marks, or spaces,
     * and does not contain the word "seznam".
     * @param title The title to check.
     * @return True if the title is valid, false otherwise.
     */
    private static boolean validChars(String title) {
        String lowerName = title.replace("Kategorie:", "").toLowerCase();
        return lowerName.matches("[\\p{L}\\p{N}\\p{M} ]+") && !lowerName.contains("seznam");
    }

    /**
     * Parses a JSON object from the wiki API.
     * @param category The category to get the JSON for.
     * @param baseURL The base URL of the wiki API.
     * @return The parsed JSON object, or null if the document is empty.
     */
    private static JsonObject parseJson(Category category, String baseURL) {
        String name = category.getName();
        String url = baseURL + "Kategorie:" + name;
        Document document = JsoupGetter.getDocument(url, wikiCache);

        if (document != JsoupGetter.EMPTY_DOCUMENT) {
            String jsonString = document.body().text();
            return new Gson().fromJson(jsonString, JsonObject.class);
        }
        return null;
    }

    /**
     * Gets the main article of a specified category.
     * @param category The category to get the main article of.
     * @return The main article of the category, or null if the category does not have a main article.
     */
    protected static Article getMainArticle(Category category) {
        JsonObject jsonObject = parseJson(category, mainArticleURL);
        if (jsonObject != null) {
            // get first page
            JsonObject pages = jsonObject.getAsJsonObject("query").getAsJsonObject("pages");
            String firstPageId = pages.keySet().iterator().next();
            JsonObject firstPage = pages.getAsJsonObject(firstPageId);
            JsonArray revisions = firstPage.getAsJsonArray("revisions");

            if (!revisions.isEmpty()) {
                // get first revision
                JsonObject revision = revisions.get(0).getAsJsonObject();
                String content = revision.get("*").getAsString();
                return getMainArticleFromContent(category, content);
            }
        }
        return null;
    }

    /**
     * Gets the main article from the content of a specified category.
     * @param category The category to get the main article from.
     * @param content The content to get the main article from.
     * @return The main article, or null if the content does not contain a main article.
     */
    private static Article getMainArticleFromContent(Category category, String content) {
        String mainArticleTag = "{{Hlavní článek|";
        int startIndex = content.indexOf(mainArticleTag);
        if (startIndex != -1) {
            startIndex += mainArticleTag.length();
            int endIndex = content.indexOf("}}", startIndex);
            if (endIndex != -1) {
                String[] mainArticles = content.substring(startIndex, endIndex).split("\\|");
                return Arrays.stream(mainArticles)
                        .filter(WikiGetter::validChars).min(Comparator.comparingInt(String::length))
                        .map(title -> new Article(title, category.getPath()))
                        .orElse(null);
            }
        }
        return null;
    }
}
