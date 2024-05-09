package cz.cuni.mff.desitka.wiki.types;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * This class represents a value category in the wiki.
 * It extends the Category class and includes additional functionality for managing keyword-specific properties.
 */
public class ValueCategory extends Category {
    private final String keyword;
    private final String nameWithoutKeyword;

    /**
     * Constructs a ValueCategory with a specified path and keyword.
     * The name of the ValueCategory is derived from the filename of the path.
     * The name without the keyword is also set.
     * @param path The path of the ValueCategory.
     * @param keyword The keyword of the ValueCategory.
     */
    public ValueCategory(Path path, String keyword) {
        super(path);
        this.keyword = keyword;
        this.nameWithoutKeyword = setNameWithoutKeyword(keyword);
    }

    /**
     * Sets the name of the ValueCategory without the keyword.
     * The name is split into parts, and the parts that do not match the keyword are joined together.
     * @param keyword The keyword to exclude from the name.
     * @return The name of the ValueCategory without the keyword.
     */
    private String setNameWithoutKeyword(String keyword) {
        String[] nameParts = name.split("_");
        String[] keywordParts = keyword.split("_");
        int start, end;

        if (nameParts[0].equalsIgnoreCase(keywordParts[0])) {
            start = keywordParts.length;
            end = nameParts.length;
        }
        else {
            start = 0;
            end = nameParts.length - keywordParts.length;
        }

        return String.join("_", Arrays.copyOfRange(nameParts, start, end));
    }

    /**
     * Returns the keyword of the ValueCategory.
     * @return The keyword of the ValueCategory.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the name of the ValueCategory without the keyword.
     * @return The name of the ValueCategory without the keyword.
     */
    public String getNameWithoutKeyword() {
        return nameWithoutKeyword;
    }
}