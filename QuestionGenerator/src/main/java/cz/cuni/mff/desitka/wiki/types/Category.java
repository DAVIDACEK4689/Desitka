package cz.cuni.mff.desitka.wiki.types;

import java.nio.file.Path;

/**
 * This class represents a category in the wiki.
 * It extends the WikiObject class and includes additional functionality for managing category-specific properties.
 */
public class Category extends WikiObject {

    /**
     * Constructs a Category with a specified title and path.
     * The title of the Category is modified to remove the "Kategorie:" prefix.
     * @param title The title of the Category.
     * @param path The path of the Category.
     */
    public Category(String title, Path path) {
        super(title.replace("Kategorie:", ""), path);
    }

    /**
     * Constructs a Category with a specified path.
     * The title of the Category is derived from the filename of the path.
     * @param path The path of the Category.
     */
    public Category(Path path) {
        super(path);
    }

    /**
     * Checks if this Category is a question category.
     * A Category is considered a question category if it has a question category name, its name does not start with a digit,
     * and both parts of its name (split by "_podle_") have at most 2 subparts.
     * @return True if this Category is a question category, false otherwise.
     */
    public boolean isQuestionCategory() {
        if (hasQuestionCategoryName() && !nameStartsWithDigit()) {
            String[] parts = name.split("_podle_");
            String[] firstPart = parts[0].split("_");
            String[] secondPart = parts[1].split("_");
            return firstPart.length <= 2 && secondPart.length <= 2;
        }
        return false;
    }

    /**
     * Checks if this Category has a question category name.
     * A Category has a question category name if its name contains "_podle_" and does not start with "Kategorie".
     * @return True if this Category has a question category name, false otherwise.
     */
    private boolean hasQuestionCategoryName() {
        return name.contains("_podle_") && !name.startsWith("Kategorie");
    }

    /**
     * Checks if the name of this Category starts with a digit.
     * @return True if the name of this Category starts with a digit, false otherwise.
     */
    private boolean nameStartsWithDigit() {
        return name.matches("\\d.*");
    }

    /**
     * Checks if this Category is not a specify category.
     * A Category is not a specify category if its name does not contain "podle".
     * @return True if this Category is not a specify category, false otherwise.
     */
    public boolean isNotSpecifyCategory() {
        return !isSpecifyCategory();
    }

    /**
     * Checks if this Category is a specify category.
     * A Category is a specify category if its name contains "podle".
     * @return True if this Category is a specify category, false otherwise.
     */
    public boolean isSpecifyCategory() {
        return name.contains("podle");
    }
}