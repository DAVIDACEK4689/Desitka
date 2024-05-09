package cz.cuni.mff.desitka.wiki.getters;

import cz.cuni.mff.desitka.dictionaries.Dictionary;
import cz.cuni.mff.desitka.dictionaries.Wiktionary;
import cz.cuni.mff.desitka.wiki.types.Article;
import cz.cuni.mff.desitka.wiki.types.Category;
import cz.cuni.mff.desitka.wiki.types.ValueCategory;
import cz.cuni.mff.desitka.question.QuestionTextCreator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class extends WikiGetter and is responsible for getting values from a wiki.
 */
public class ValuesWikiGetter extends WikiGetter {

    /**
     * This method gets values from a directory and prints the total count of values loaded.
     * @param directory The directory to get values from.
     */
    public static void getValues(String directory) {
        AtomicInteger counter = new AtomicInteger(0);
        try {
            System.out.println("Loading values...");
            Files.walk(Paths.get(directory))
                    .collect(Collectors.toList())
                    .parallelStream()
                    .map(Category::new)
                    .filter(Category::isQuestionCategory)
                    .forEach(category -> getValues(category, counter));

            // values loaded
            System.out.println("All values loaded, count: " + counter.get());
            System.out.println();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets values for a specific category.
     * @param category The category to get values for.
     * @param counter The counter to increment when a value is loaded.
     */
    private static void getValues(Category category, AtomicInteger counter) {
        // Get values
        Category[] childCategories = getChildCategories(category)
                .stream()
                .filter(Category::isNotSpecifyCategory)
                .toArray(Category[]::new);

        // Get keyword
        String keyword = getKeyword(category, childCategories);
        saveCategoryData(category, keyword);

        if (keyword != null) {
            List<ValueCategory> keywordChildCategories = getKeywordCategories(childCategories, keyword);
            if (keywordChildCategories.size() > 3) {
                // save category data
                boolean adjectiveValues = !keywordChildCategories.get(0).getLowerName().startsWith(keyword);
                updateKeyword(category, keywordChildCategories, adjectiveValues);

                if (QuestionTextCreator.createQuestionText(category, adjectiveValues)) {
                    parseValues(keywordChildCategories, category, adjectiveValues);
                    System.out.println("Values loaded for " + category.getName());
                    counter.incrementAndGet();
                }
            }
        }
    }

    /**
     * This method updates the keyword for a category.
     * @param category The category to update the keyword for.
     * @param keywordChildCategories The child categories of the keyword.
     * @param adjectiveValues A boolean indicating whether the values are adjectives.
     */
    private static void updateKeyword(Category category, List<ValueCategory> keywordChildCategories, boolean adjectiveValues) {
        if (!adjectiveValues) {
            String oldKeyword = keywordChildCategories.get(0).getKeyword();
            String newKeyword = getLongestCommonPrefix(keywordChildCategories);

            // Save new keyword
            if (!oldKeyword.equals(newKeyword)) {
                List<ValueCategory> newKeywordChildCategories = keywordChildCategories.stream()
                        .map(valueCategory -> new ValueCategory(valueCategory.getPath(), newKeyword))
                        .collect(Collectors.toList());

                // Update categories
                keywordChildCategories.clear();
                keywordChildCategories.addAll(newKeywordChildCategories);
                saveKeyword(category, newKeyword);
            }
        }
    }

    /**
     * This method gets the longest common prefix from a list of ValueCategory.
     * @param keywordChildCategories The list of ValueCategory to get the longest common prefix from.
     * @return The longest common prefix.
     */
    private static String getLongestCommonPrefix(List<ValueCategory> keywordChildCategories) {
        ValueCategory firstCategory = keywordChildCategories.get(0);
        String prefix = firstCategory.getLowerName();

        for (int i = 1; i < keywordChildCategories.size(); i++) {
            while (keywordChildCategories.get(i).getLowerName().indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.lastIndexOf("_"));
            }
        }
        return prefix;
    }

    /**
     * This method gets the keyword for a category.
     * @param category The category to get the keyword for.
     * @param childCategories The child categories of the category.
     * @return The keyword for the category.
     */
    private static String getKeyword(Category category, Category[] childCategories) {
        HashMap<String, Integer> map = new HashMap<>();
        String[] possibleKeywords = category.getName()
                .toLowerCase()
                .split("_podle_")[0]
                .split("_");
        String[] childCategoriesNamesLower = Arrays.stream(childCategories)
                .map(Category::getLowerName)
                .toArray(String[]::new);

        // Find keyword
        addKeywords(map, possibleKeywords);
        countOccurrences(childCategoriesNamesLower, map);
        List<Map.Entry<String, Integer>> list = sortMap(map);
        return list.isEmpty() ? null : list.get(0).getKey();
    }

    /**
     * This method adds keywords to a map.
     * @param map The map to add keywords to.
     * @param possibleKeywords The possible keywords to add to the map.
     */
    private static void addKeywords(HashMap<String, Integer> map, String[] possibleKeywords) {
        // Add single keywords
        for (String keyword : possibleKeywords) {
            map.put(keyword, 0);
        }

        // Add longer keywords
        for (int i = 1; i < possibleKeywords.length; i++) {
            String keyword = String.join("_", Arrays.copyOfRange(possibleKeywords, 0, i+1));
            map.put(keyword, 0);
        }
    }

    /**
     * This method counts the occurrences of each keyword in a list of category names.
     * @param childCategoriesNamesLower The list of category names to count occurrences in.
     * @param map The map to update with the count of occurrences.
     */
    private static void countOccurrences(String[] childCategoriesNamesLower, HashMap<String, Integer> map) {
        // Count occurrences
        for (String title : childCategoriesNamesLower) {
            for (String keyword : map.keySet()) {
                if (title.startsWith(keyword) || title.endsWith(keyword)) {
                    map.put(keyword, map.get(keyword) + 1);
                }
            }
        }
    }

    /**
     * This method sorts a map based on the length of the keyword times its occurrences.
     * @param map The map to sort.
     * @return A list of map entries sorted by the length of the keyword times its occurrences.
     */
    private static List<Map.Entry<String, Integer>> sortMap(HashMap<String, Integer> map) {
        // Convert HashMap to List of Map.Entry
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        // Sort the list based on keywordLength * occurrences
        list.sort((o1, o2) -> {
            int occurrencesTimesLength1 = o1.getKey().length() * o1.getValue();
            int occurrencesTimesLength2 = o2.getKey().length() * o2.getValue();
            return Integer.compare(occurrencesTimesLength2, occurrencesTimesLength1);
        });
        return list;
    }

    /**
     * This method saves the keyword and an empty question text for a category.
     * @param category The category to save the keyword and empty question text for.
     * @param keyword The keyword to save.
     */
    private static void saveCategoryData(Category category, String keyword) {
        saveKeyword(category, keyword);
        saveEmptyQuestionText(category);
    }

    /**
     * This method saves a keyword for a category.
     * @param category The category to save the keyword for.
     * @param keyword The keyword to save.
     */
    private static void saveKeyword(Category category, String keyword) {
        try {
            // Save file specifying the keyword
            String keywordPath = category.getPath() + "/keyword.txt";
            String writeString = (keyword == null) ? "" : keyword;
            Files.write(Paths.get(keywordPath), writeString.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method saves an empty question text for a category.
     * @param category The category to save the empty question text for.
     */
    private static void saveEmptyQuestionText(Category category) {
        try {
            // Create value file
            String valuePath = category.getPath() + "/question_text.txt";
            Files.write(Paths.get(valuePath), "".getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets the keyword categories for a list of child categories.
     * @param childCategories The child categories to get the keyword categories for.
     * @param keyword The keyword to get the categories for.
     * @return A list of ValueCategory that are keyword categories.
     */
    private static List<ValueCategory> getKeywordCategories(Category[] childCategories, String keyword) {
        List<ValueCategory> categories1 = getStartingKeywordCategories(childCategories, keyword);
        List<ValueCategory> categories2 = getEndingKeywordCategories(childCategories, keyword);
        return categories1.size() >= categories2.size()
                ? categories1
                : categories2;
    }

    /**
     * This method gets the starting keyword categories for a list of child categories.
     * @param childCategories The child categories to get the starting keyword categories for.
     * @param keyword The keyword to get the categories for.
     * @return A list of ValueCategory that are starting keyword categories.
     */
    private static List<ValueCategory> getStartingKeywordCategories(Category[] childCategories, String keyword) {
        return Arrays.stream(childCategories)
                .filter(childCategory -> childCategory.getLowerName().startsWith(keyword + "_"))
                .map(childCategory -> new ValueCategory(childCategory.getPath(), keyword))
                .collect(Collectors.toList());
    }

    /**
     * This method gets the ending keyword categories for a list of child categories.
     * @param childCategories The child categories to get the ending keyword categories for.
     * @param keyword The keyword to get the categories for.
     * @return A list of ValueCategory that are ending keyword categories.
     */
    private static List<ValueCategory> getEndingKeywordCategories(Category[] childCategories, String keyword) {
        return Arrays.stream(childCategories)
                .filter(valueCategory -> valueCategory.getLowerName().endsWith(keyword))
                .map(childCategory -> new ValueCategory(childCategory.getPath(), keyword))
                .collect(Collectors.toList());
    }

    /**
     * This method parses values for a list of ValueCategory.
     * @param valueCategories The list of ValueCategory to parse values for.
     * @param category The category to parse values for.
     * @param adjectiveValues A boolean indicating whether the values are adjectives.
     */
    private static void parseValues(List<ValueCategory> valueCategories, Category category, boolean adjectiveValues) {
        // parse compound
        if (!adjectiveValues) {
            valueCategories.forEach(ValuesWikiGetter::parseNormal);
            return;
        }
        // parse adjective by genus
        Wiktionary.GENUS genus = getAdjectiveGenus(category);
        if (genus != null) {
            valueCategories.forEach(valueCategory -> parseAdjective(valueCategory, genus));
        }
    }

    /**
     * This method parses normal values for a ValueCategory.
     * @param valueCategory The ValueCategory to parse normal values for.
     */
    private static void parseNormal(ValueCategory valueCategory) {
        // Get words longer than 2 characters
        String[] words = Arrays.stream(valueCategory.getNameWithoutKeyword().split("_"))
                .filter(word -> word.length() >= 3)
                .toArray(String[]::new);

        if (words.length != 0) {
            if (Dictionary.allWordsKnown(words)) {
                getValueFromDictionary(valueCategory, words);
                return;
            }
            getValueFromWikipedia(valueCategory);
        }
    }

    /**
     * This method gets a value from a dictionary for a ValueCategory.
     * @param valueCategory The ValueCategory to get a value for.
     * @param words The words to get a value for.
     */
    private static void getValueFromDictionary(ValueCategory valueCategory, String[] words) {
        if (words.length <= 2) {
            String value = (words.length == 1) ? Dictionary.getNoun(words[0], 1, -1)
                                               : Dictionary.getCompound(words[0], words[1], 1, -1);
            if (value != null) {
                String[] values = value.split(" ");
                String capitalizeValue = fixCapitals(values, words);
                saveValue(valueCategory, capitalizeValue);
            }
        }
    }

    /**
     * This method fixes the capitalization of a list of values based on a list of musters.
     * @param values The list of values to fix the capitalization of.
     * @param musters The list of musters to use for fixing the capitalization.
     * @return A string of values with fixed capitalization.
     */
    private static String fixCapitals(String[] values, String[] musters) {
        String[] result = new String[values.length];
        int musterIndex = 0;

        for (int i = 0; i < values.length; i++) {
            if (values[i].length() < 3) {
                result[i] = values[i];
                continue;
            }
            String muster = musters[musterIndex++];
            result[i] = Character.isUpperCase(muster.charAt(0)) ? uppercase(values[i])
                                                                : lowercase(values[i]);
        }
        return String.join(" ", result);
    }

    /**
     * This method converts a string to lowercase.
     * @param valueWord The string to convert to lowercase.
     * @return The lowercase string.
     */
    private static String lowercase(String valueWord) {
        return valueWord.substring(0, 1).toLowerCase() + valueWord.substring(1);
    }

    /**
     * This method converts a string to uppercase.
     * @param value The string to convert to uppercase.
     * @return The uppercase string.
     */
    private static String uppercase(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    /**
     * This method gets a value from Wikipedia for a ValueCategory.
     * @param valueCategory The ValueCategory to get a value for.
     */
    private static void getValueFromWikipedia(ValueCategory valueCategory) {
        if (!checkParentCategories(valueCategory)) {
            checkMainArticle(valueCategory);
        }
    }

    /**
     * This method checks the main article for a ValueCategory.
     * @param valueCategory The ValueCategory to check the main article for.
     */
    private static void checkMainArticle(ValueCategory valueCategory) {
        // Get main article
        Article article = getMainArticle(valueCategory);

        if (article != null) {
            // Get starting letters
            String[] mainArticleLetters = getStartingLetters(article.getName());
            String[] valueCategoryLetters = getStartingLetters(valueCategory.getNameWithoutKeyword());

            // check match
            if (absoluteMatch(valueCategoryLetters, mainArticleLetters)) {
                String[] values = article.getName().split("_");
                String value = fixCapitals(values, valueCategoryLetters);
                saveValue(valueCategory, value);
            }
        }
    }

    /**
     * This method checks the parent categories for a ValueCategory.
     * @param valueCategory The ValueCategory to check the parent categories for.
     * @return A boolean indicating whether the parent categories were checked.
     */
    private static boolean checkParentCategories(ValueCategory valueCategory) {
        List<Category> parentCategories = getParentCategories(valueCategory);
        String[] searchedLetters = getStartingLetters(valueCategory.getNameWithoutKeyword());
        LinkedList<Category> queue = new LinkedList<>(parentCategories);

        while (!queue.isEmpty()) {
            Category parentCategory = queue.poll();
            String[] parentCategoryLetters = getStartingLetters(parentCategory.getName());

            // check absolute match
            if (absoluteMatch(searchedLetters, parentCategoryLetters)) {
                String[] values = parentCategory.getName().split("_");
                String value = fixCapitals(values, searchedLetters);
                saveValue(valueCategory, value);
                return true;
            }

            // check partial match
            if (partialMatch(searchedLetters, parentCategoryLetters)) {
                queue.addAll(getParentCategories(parentCategory));
            }
        }
        return false;
    }

    /**
     * This method checks if two arrays match absolutely.
     * @param array1 The first array to check.
     * @param array2 The second array to check.
     * @return A boolean indicating whether the two arrays match absolutely.
     */
    private static boolean absoluteMatch(String[] array1, String[] array2) {
        String string1 = String.join("", array1);
        String string2 = String.join("", array2);
        return string1.equalsIgnoreCase(string2);
    }

    /**
     * This method checks if a short array partially matches a long array.
     * @param shortArray The short array to check.
     * @param longArray The long array to check.
     * @return A boolean indicating whether the short array partially matches the long array.
     */
    private static boolean partialMatch(String[] shortArray, String[] longArray) {
        String string1 = String.join("", shortArray);
        int difference = longArray.length - shortArray.length;
        int copyLength = shortArray.length;

        if (difference > 0) {
            for (int i = 0; i < difference + 1; i++) {
                String[] subArray = Arrays.copyOfRange(longArray, i, i + copyLength);
                String string2 = String.join("", subArray);
                if (string1.equalsIgnoreCase(string2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method saves a value for a ValueCategory.
     * @param valueCategory The ValueCategory to save a value for.
     * @param value The value to save.
     */
    private static void saveValue(ValueCategory valueCategory, String value) {
        try {
            // Create directories
            Files.createDirectory(valueCategory.getPath());

            // Create value file
            String valuePath = valueCategory.getPath() + "/value.txt";
            Files.write(Paths.get(valuePath), value.replace("_", " ").getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method parses an adjective for a ValueCategory.
     * @param valueCategory The ValueCategory to parse an adjective for.
     * @param genus The genus of the adjective.
     */
    private static void parseAdjective(ValueCategory valueCategory, Wiktionary.GENUS genus) {
        String nameWithoutKeyword = valueCategory.getNameWithoutKeyword();
        String adjective = Dictionary.getAdjective(nameWithoutKeyword, genus, 4, 1);
        if (adjective != null) {
            saveValue(valueCategory, adjective);
        }
    }

    /**
     * This method gets the adjective genus for a category.
     * @param category The category to get the adjective genus for.
     * @return The adjective genus for the category.
     */
    private static Wiktionary.GENUS getAdjectiveGenus(Category category) {
        String categoryName = category.getName();
        String[] nameParts = categoryName.split("_");
        for (int i = 1; i < nameParts.length; i++) {
            if (nameParts[i].equals("podle")) {
                String genusWord = nameParts[i - 1];
                return Dictionary.getGenus(genusWord);
            }
        }
        throw new RuntimeException("Unable to find genus-word for: " + categoryName);
    }
}
