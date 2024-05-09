package cz.cuni.mff.desitka.wiki.getters;

import cz.cuni.mff.desitka.wiki.PageViewer;
import cz.cuni.mff.desitka.wiki.types.Article;
import cz.cuni.mff.desitka.wiki.types.Category;
import cz.cuni.mff.desitka.wiki.types.ValueCategory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class extends WikiGetter and is responsible for getting keys from a wiki.
 */
public class KeysWikiGetter extends WikiGetter {

    /**
     * This method gets keys from a directory and prints the total count of keys loaded.
     * @param directory The directory to get keys from.
     * @param viewsLimit The limit of views for the keys.
     */
    public static void getKeys(String directory, int viewsLimit) {
        AtomicInteger counter = new AtomicInteger(0);
        try {
            System.out.println("Loading keys...");
            Files.walk(Paths.get(directory))
                    .collect(Collectors.toList())
                    .parallelStream()
                    .map(Category::new)
                    .filter(Category::isQuestionCategory)
                    .forEach(category -> getKeys(category, viewsLimit, counter));

            // keys loaded
            System.out.println("All keys loaded, count: " + counter.get());
            System.out.println();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets keys for a specific category.
     * @param category The category to get keys for.
     * @param viewsLimit The limit of views for the keys.
     * @param counter The counter to increment when a key is loaded.
     */
    private static void getKeys(Category category, int viewsLimit, AtomicInteger counter) {
        try {
            Path path = category.getPath();
            String keyword = Files.lines(path.resolve("keyword.txt"))
                    .findFirst()
                    .orElse(null);
            String questionText = Files.lines(path.resolve("question_text.txt"))
                    .findFirst()
                    .orElse(null);

            if (questionText != null) {
                Files.list(path)
                        .collect(Collectors.toList())
                        .stream()
                        .filter(file -> !file.getFileName().toString().contains("podle"))
                        .filter(file -> !file.getFileName().toString().endsWith(".txt"))
                        .map(valuePath -> new ValueCategory(valuePath, keyword))
                        .forEach(valueCategory -> getKeys(valueCategory, viewsLimit));

                // Keys for category loaded
                System.out.println("Keys loaded for " + category.getName());
                counter.incrementAndGet();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets keys for a specific ValueCategory.
     * @param valueCategory The ValueCategory to get keys for.
     * @param viewsLimit The limit of views for the keys.
     */
    private static void getKeys(ValueCategory valueCategory, int viewsLimit) {
        // get keys
        Set<Article> keys;
        keys = getPossibleKeys(valueCategory);
        keys = addKeysViews(keys, viewsLimit);
        keys = removeTrivialKeys(keys, valueCategory.getNameWithoutKeyword());

        // save keys
        saveKeys(keys, valueCategory.getPath());
    }

    /**
     * This method gets possible keys for a specific ValueCategory.
     * @param valueCategory The ValueCategory to get possible keys for.
     * @return A set of possible keys.
     */
    private static Set<Article> getPossibleKeys(ValueCategory valueCategory) {
        // Create a set of articles
        Set<Article> keys = new HashSet<>();
        String keyword = valueCategory.getKeyword();
        LinkedList<Category> queue = new LinkedList<>();
        queue.add(valueCategory);

        // Process the queue
        while (!queue.isEmpty()) {
            Category category = queue.poll();
            addArticleKeys(keys, category);
            addCategoryKeys(keys, category);
            updateQueue(queue, category, keyword);
        }
        return keys;
    }

    /**
     * This method adds article keys to a set of keys.
     * @param keys The set of keys to add article keys to.
     * @param category The category to get article keys from.
     */
    private static void addArticleKeys(Set<Article> keys, Category category) {
        keys.addAll(getChildArticles(category));
    }

    /**
     * This method adds category keys to a set of keys.
     * @param keys The set of keys to add category keys to.
     * @param category The category to get category keys from.
     */
    private static void addCategoryKeys(Set<Article> keys, Category category) {
        keys.addAll(getChildCategories(category)
                .stream()
                .filter(Category::isNotSpecifyCategory)
                .filter(KeysWikiGetter::categoryHasArticle)
                .map(childCategory -> new Article(childCategory.getName(), childCategory.getPath()))
                .collect(Collectors.toSet()));
    }

    /**
     * This method updates a queue with categories that contain a specific keyword.
     * @param queue The queue to update.
     * @param category The category to get child categories from.
     * @param keyword The keyword to filter child categories by.
     */
    private static void updateQueue(LinkedList<Category> queue, Category category, String keyword) {
        queue.addAll(getChildCategories(category)
                .stream()
                .filter(Category::isNotSpecifyCategory)
                .filter(KeysWikiGetter::categoryHasNotArticle)
                .filter(childCategory -> childCategory.getLowerName().contains(keyword))
                .collect(Collectors.toSet()));
    }

    /**
     * This method adds views to a set of keys and filters them by a views limit.
     * @param keys The set of keys to add views to.
     * @param viewsLimit The limit of views to filter keys by.
     * @return A set of keys with added views.
     */
    private static Set<Article> addKeysViews(Set<Article> keys, int viewsLimit) {
        return keys.stream()
                .map(key -> new Article(key.getPath(), PageViewer.getViews(key.getName())))
                .filter(article -> article.getViews() > viewsLimit)
                .collect(Collectors.toSet());
    }

    /**
     * This method removes trivial keys from a set of keys.
     * @param keys The set of keys to remove trivial keys from.
     * @param nameWithoutKeyword The name without keyword to filter keys by.
     * @return A set of keys without trivial keys.
     */
    private static Set<Article> removeTrivialKeys(Set<Article> keys, String nameWithoutKeyword) {
        return keys.stream()
                .filter(key -> !containsSubstring(key.getName(), nameWithoutKeyword))
                .collect(Collectors.toSet());
    }

    /**
     * This method checks if a word contains a substring.
     * @param firstWord The word to check.
     * @param secondWord The substring to check for.
     * @return A boolean indicating whether the word contains the substring.
     */
    private static boolean containsSubstring(String firstWord, String secondWord) {
        String[] firstWords = firstWord.toLowerCase().split("_");
        String[] secondWords = getStartingLetters(secondWord.toLowerCase());

        for (String word1 : firstWords) {
            for (String word2 : secondWords) {
                if (word1.contains(word2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method saves a set of keys to a path.
     * @param keys The set of keys to save.
     * @param path The path to save the keys to.
     */
    private static void saveKeys(Set<Article> keys, Path path) {
        try {
            Path keysPath = path.resolve("keys");
            Files.createDirectory(keysPath);
            keys.forEach(key -> key.save(keysPath));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
