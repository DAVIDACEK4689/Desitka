package cz.cuni.mff.desitka.wiki.getters;

import cz.cuni.mff.desitka.wiki.types.Category;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.Files.createDirectory;

/**
 * This class extends WikiGetter and is responsible for getting categories from a wiki.
 */
public class CategoriesWikiGetter extends WikiGetter {

    /**
     * This method gets categories from a directory and prints a message when all categories are loaded.
     * @param directory The directory to get categories from.
     * @param title The title of the category to start getting categories from.
     */
    public static void getCategories(String directory, String title) {
        System.out.println("Loading categories...");
        cleanStartingDirectory(directory);

        // Load all categories
        Category category = new Category(title, Paths.get(directory));
        ConcurrentHashMap<String, Boolean> visitedCategories = new ConcurrentHashMap<>();
        getCategories(category, visitedCategories);

        System.out.println("All categories loaded");
        System.out.println();
    }

    /**
     * This method gets categories for a specific category and updates a map of visited categories.
     * @param category The category to get categories for.
     * @param visitedCategories The map of visited categories to update.
     */
    public static void getCategories(Category category, ConcurrentHashMap<String, Boolean> visitedCategories) {
        // If the category is not already in the set
        if (visitedCategories.putIfAbsent(category.getName(), true) == null) {
            createCategoryDirectory(category);
            getChildCategories(category).parallelStream()
                    .filter(Category::isSpecifyCategory)
                    .forEach(childCategory -> getCategories(childCategory, visitedCategories));
        }
    }

    /**
     * This method creates a directory for a category.
     * @param category The category to create a directory for.
     */
    private static void createCategoryDirectory(Category category) {
        try {
            createDirectory(category.getPath());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
