package cz.cuni.mff.desitka.dictionaries;

import cz.cuni.mff.desitka.JsoupGetter;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class extends Wiktionary and provides methods to interact with a dictionary.
 * It includes methods to get nouns, adjectives, and compounds.
 * It also includes methods to check if all words are known and to get the genus of a word.
 */
public class Dictionary extends Wiktionary {
    private static final String BASE_URL = "https://www.nechybujte.cz/slovnik-soucasne-cestiny/";
    private static final ConcurrentHashMap<String, Document> dictionaryCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> adjectivesCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> nounsCache = new ConcurrentHashMap<String, String>() {{
        put("let4", "rok");
    }};

    /**
     * This method retrieves elements from a given word.
     * @param word The word to retrieve elements from.
     * @return Elements from the word if found, null otherwise.
     */
    private static Elements getElements(String word) {
        Document document = JsoupGetter.getDocument(BASE_URL + word, dictionaryCache);
        Element entryWrapper = document.getElementById("entry-wrapper");
        return (entryWrapper != null) ? entryWrapper.select("div.ssc_head, div.ssc_grps") : null;
    }

    /**
     * This method gets a compound of two words.
     * @param word1 The first word.
     * @param word2 The second word.
     * @param requiredCase The required case.
     * @param currentCase The current case.
     * @return The compound of the two words if found, null otherwise.
     */
    public static String getCompound(String word1, String word2, int requiredCase, int currentCase) {
        Wiktionary.GENUS genus = getGenus(word2);
        if (genus != null) {
            String adjective = getAdjective(word1, genus, requiredCase, currentCase);
            String noun = getNoun(word2, requiredCase, currentCase);
            return (adjective != null && noun != null) ? (adjective + " " + noun) : null;
        }
        return null;
    }

    /**
     * This method gets possible words from a given row.
     * @param row The row to get possible words from.
     * @return A stream of possible words.
     */
    private static Stream<String> getPossibleWords(Element row) {
        Stream.Builder<String> builder = Stream.builder();
        addWordsToBuilder(row.select("span.ssc_entr"), builder);
        addWordsToBuilder(row.select("span.ssc_phrs"), builder);
        addWordsToBuilder(row.select("span.ssc_e"), builder);
        return builder.build();
    }

    /**
     * This method adds words to a builder.
     * @param rows The rows to add words from.
     * @param builder The builder to add words to.
     */
    private static void addWordsToBuilder(Elements rows, Stream.Builder<String> builder) {
        rows.textNodes().stream()
                .map(org.jsoup.nodes.TextNode::text)
                .filter(value -> !value.isEmpty())
                .findFirst()
                .ifPresent(builder::add);
    }

    /**
     * This method gets a noun from a given form.
     * @param form The form to get the noun from.
     * @param requiredCase The required case.
     * @param currentCase The current case.
     * @return The noun if found, null otherwise.
     */
    public static String getNoun(String form, int requiredCase, int currentCase) {
        // Check cache
        String cachedNoun = nounsCache.get(form + requiredCase);
        if (cachedNoun != null) {
            return cachedNoun.isEmpty() ? null : cachedNoun;
        }

        // get noun
        cachedNoun = getPasswords(form).stream()
                .map(Dictionary::getElements)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Dictionary::isNoun)
                .flatMap(Dictionary::getPossibleWords)
                .filter(noun -> Wiktionary.isFormOfNoun(form, noun, currentCase))
                .findFirst()
                .map(noun -> Wiktionary.getNounForm(noun, requiredCase))
                .orElse("");

        // Save to cache
        nounsCache.putIfAbsent(form + requiredCase, cachedNoun);
        return cachedNoun.isEmpty() ? null : cachedNoun;
    }

    /**
     * This method checks if an element is a noun.
     * @param element The element to check.
     * @return True if the element is a noun, false otherwise.
     */
    private static boolean isNoun(Element element) {
        String genus = element.select("span.ssc_morf").text();
        return genus.equals("m") || genus.equals("ž") || genus.equals("s");
    }

    /**
     * This method gets an adjective from a given form.
     * @param form The form to get the adjective from.
     * @param genus The genus of the adjective.
     * @param requiredCase The required case.
     * @param currentCase The current case.
     * @return The adjective if found, null otherwise.
     */
    public static String getAdjective(String form, Wiktionary.GENUS genus, int requiredCase, int currentCase) {
        // Check cache
        String cachedAdjective = adjectivesCache.get(form + genus + requiredCase);
        if (cachedAdjective != null) {
            return cachedAdjective.isEmpty() ? null : cachedAdjective;
        }

        // Get adjective
        Elements rows = getElements(form);
        if (rows != null) {
            cachedAdjective = rows.stream()
                    .filter(Dictionary::isAdjective)
                    .flatMap(Dictionary::getPossibleWords)
                    .filter(adjective -> Wiktionary.isFormOfAdjective(form, adjective, currentCase))
                    .findFirst()
                    .map(adjective -> Wiktionary.getAdjectiveForm(adjective, genus, requiredCase))
                    .orElse("");

            // Save to cache
            adjectivesCache.putIfAbsent(form + genus + requiredCase, cachedAdjective);
            return cachedAdjective.isEmpty() ? null : cachedAdjective;
        }
        return null;
    }

    /**
     * This method checks if an element is an adjective.
     * @param element The element to check.
     * @return True if the element is an adjective, false otherwise.
     */
    private static boolean isAdjective(Element element) {
        String genus = element.select("span.ssc_morf").text();
        return genus.equals("příd.");
    }

    /**
     * This method gets passwords from a given word.
     * @param word The word to get passwords from.
     * @return A list of passwords.
     */
    @NotNull
    private static List<String> getPasswords(String word) {
        // Get url
        Document document = JsoupGetter.getDocument(BASE_URL + word, dictionaryCache);
        Element element = document.select("div.mcardlc.mcardlcs").first();

        // Get passwords
        if (element != null) {
            Elements spans = element.select("span.bspan");
            return spans.stream()
                    .map(Element::ownText)
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * This method gets the genus of a given value.
     * @param value The value to get the genus from.
     * @return The genus if found, null otherwise.
     */
    public static GENUS getGenus(String value) {
        String noun = getNoun(value, 1, -1);
        return noun != null ? Wiktionary.getGenus(noun) : null;
    }

    /**
     * This method checks if all words are known.
     * @param words The words to check.
     * @return True if all words are known, false otherwise.
     */
    public static boolean allWordsKnown(String[] words) {
        return Stream.of(words).noneMatch(word -> getPasswords(word).isEmpty());
    }
}
