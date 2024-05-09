package cz.cuni.mff.desitka.dictionaries;

import cz.cuni.mff.desitka.JsoupGetter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * This class provides methods to interact with Wiktionary.
 * It includes methods to get noun and adjective forms, check if a variant is a form of a noun or adjective, and get the genus of a noun.
 */
public class Wiktionary {
    private static final String WIKTIONARY_URL = "https://cs.wiktionary.org/wiki/";
    private static final ConcurrentHashMap<String, Document> wiktionaryCache = new ConcurrentHashMap<>();

    /**
     * This enum represents the genus of a word.
     */
    public enum GENUS {
        /**
         * The masculine animate genus.
         */
        MASCULINE_ANIMATE,

        /**
         * The masculine inanimate genus.
         */
        MASCULINE_INANIMATE,

        /**
         * The feminine genus.
         */
        FEMININE,

        /**
         * The neuter genus.
         */
        NEUTER
    }

    /**
     * This method retrieves the adjective table for a given adjective.
     * @param adjective The adjective to retrieve the table for.
     * @return The adjective table if found, null otherwise.
     */
    private static Element getAdjectiveTable(String adjective) {
        Document document = getDocument(adjective);
        Elements tables = document.select("table.deklinace.adjektivum");

        // Adjectives have 9 rows
        for (Element table : tables) {
            Elements rows = table.select("tbody tr");
            if (rows.size() == 9) {
                return table;
            }
        }
        return null;
    }

    /**
     * This method retrieves the noun table for a given noun.
     * @param noun The noun to retrieve the table for.
     * @return The noun table if found, null otherwise.
     */
    private static Element getNounTable(String noun) {
        Document document = getDocument(noun);
        Elements tables = document.select("table.deklinace.substantivum");

        // Nouns have 8 rows
        for (Element table : tables) {
            Elements rows = table.select("tbody tr");
            if (rows.size() == 8) {
                return table;
            }
        }
        return null;
    }

    /**
     * This method gets the form of an adjective based on genus and grammar case.
     * @param adjective The adjective to get the form for.
     * @param genus The genus of the adjective.
     * @param grammarCase The grammar case.
     * @return The form of the adjective if found, null otherwise.
     */
    protected static String getAdjectiveForm(String adjective, GENUS genus, int grammarCase) {
        // Update the grammarCase to match the index in the table
        grammarCase = grammarCase + 1;
        Element table = getAdjectiveTable(adjective);
        if (table != null) {
            Elements rows = table.select("tbody tr");
            Elements cases = rows.get(grammarCase).select("td");

            // Return the form of the adjective based on genus
            switch (genus) {
                case MASCULINE_ANIMATE:
                    return cases.get(0).text();
                case MASCULINE_INANIMATE:
                    return cases.get(1).text();
                case FEMININE:
                    return cases.get(2).text();
                case NEUTER:
                    return cases.get(3).text();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * This method gets the form of a noun based on grammar case.
     * @param value The noun to get the form for.
     * @param grammarCase The grammar case.
     * @return The form of the noun if found, null otherwise.
     */
    protected static String getNounForm(String value, int grammarCase) {
        // Get case forms of the noun
        Element table = getNounTable(value);
        if (table != null) {
            Elements rows = table.select("tbody tr");
            Elements cases = rows.get(grammarCase).select("td");

            // Return the form of the noun
            String singular = cases.get(0).text();
            return singular.equals("—") ? null : singular.split(" / ")[0];
        }
        return null;
    }

    /**
     * This method checks if a variant is a form of an adjective.
     * @param variant The variant to check.
     * @param adjective The adjective to check against.
     * @param currentCase The current case.
     * @return True if the variant is a form of the adjective, false otherwise.
     */
    protected static boolean isFormOfAdjective(String variant, String adjective, int currentCase) {
        // Get case forms of the noun
        String lowerVariant = variant.toLowerCase();
        Element table = getAdjectiveTable(adjective);
        if (table != null) {
            Elements rows = table.select("tbody tr");
            int rangeStart = (currentCase == -1) ? 2 : currentCase + 1;
            int rangeEnd = (currentCase == -1) ? 9 : currentCase + 2;

            return IntStream.range(rangeStart, rangeEnd)
                    .mapToObj(i -> rows.get(i).select("td"))
                    .flatMap(cases -> IntStream.range(0, 8).mapToObj(cases::get))
                    .anyMatch(caseElement -> caseElement.text().toLowerCase().equals(lowerVariant));
        }
        return false;
    }

    /**
     * This method checks if a variant is a form of a noun.
     * @param variant The variant to check.
     * @param noun The noun to check against.
     * @param currentCase The current case.
     * @return True if the variant is a form of the noun, false otherwise.
     */
    protected static boolean isFormOfNoun(String variant, String noun, int currentCase) {
        // Get case forms of the noun
        Element table = getNounTable(noun);
        String lowerVariant = variant.toLowerCase();
        if (table != null) {
            Elements rows = table.select("tbody tr");
            int rangeStart = (currentCase == -1) ? 1 : currentCase;
            int rangeEnd = (currentCase == -1) ? 8 : currentCase + 1;

            return IntStream.range(rangeStart, rangeEnd)
                    .mapToObj(i -> rows.get(i).select("td"))
                    .flatMap(cases -> IntStream.range(0, 2).mapToObj(cases::get))
                    .flatMap(element -> Arrays.stream(element.text().toLowerCase().split(" / ")))
                    .anyMatch(value -> value.equals(lowerVariant));
        }
        return false;
    }

    /**
     * This method gets the genus of a noun.
     * @param noun The noun to get the genus for.
     * @return The genus of the noun if found, null otherwise.
     */
    protected static GENUS getGenus(String noun) {
        Document document = getDocument(noun);
        Elements elements = document.select("ul li i");

        if (!elements.isEmpty()) {
            switch (elements.get(0).text()) {
                case "rod mužský životný":
                    return GENUS.MASCULINE_ANIMATE;
                case "rod mužský neživotný":
                    return GENUS.MASCULINE_INANIMATE;
                case "rod ženský":
                    return GENUS.FEMININE;
                case "rod střední":
                    return GENUS.NEUTER;
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * This method retrieves a document for a given noun.
     * @param noun The noun to retrieve the document for.
     * @return The document if found, null otherwise.
     */
    private static Document getDocument(String noun) {
        Document document = wiktionaryCache.get(noun);
        return document == null ? JsoupGetter.getDocument(WIKTIONARY_URL + noun, wiktionaryCache)
                                : document;
    }
}
