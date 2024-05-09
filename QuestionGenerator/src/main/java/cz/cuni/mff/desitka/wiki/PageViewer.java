package cz.cuni.mff.desitka.wiki;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cz.cuni.mff.desitka.JsoupGetter;
import org.jsoup.nodes.Document;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for viewing pages.
 * It uses the JsoupGetter class to get a document from a specified URL and parses the JSON from the document.
 */
public class PageViewer {
    private static final String BASE_URL = "https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/cs.wikipedia/all-access/all-agents/";
    private static final String MONTHLY = "/monthly/20230101/20231231";
    private static final ConcurrentHashMap<String, Document> pageViewerCache = new ConcurrentHashMap<>();

    /**
     * Gets the views of a page with a specified title.
     * It parses the JSON from the document of the page and calculates the average views.
     * @param title The title of the page.
     * @return The average views of the page.
     */
    public static int getViews(String title) {
        JsonArray items = parseJson(title);
        if (items != null) {
            return (int) items.asList()
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .mapToInt(item -> item.get("views").getAsInt())
                    .average()
                    .orElse(0);
        }
        return 0;
    }

    /**
     * Parses the JSON from the document of a page with a specified title.
     * @param title The title of the page.
     * @return The parsed JSON array.
     */
    private static JsonArray parseJson(String title) {
        String url = BASE_URL + title + MONTHLY;
        Document document = JsoupGetter.getDocument(url, pageViewerCache);

        if (document != JsoupGetter.EMPTY_DOCUMENT) {
            String jsonString = document.body().text();
            JsonObject jsonResponse = new Gson().fromJson(jsonString, JsonObject.class);
            return jsonResponse.getAsJsonArray("items");
        }
        return null;
    }
}