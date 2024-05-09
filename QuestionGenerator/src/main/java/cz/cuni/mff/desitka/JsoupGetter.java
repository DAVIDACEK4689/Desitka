package cz.cuni.mff.desitka;

import org.jetbrains.annotations.NotNull;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides methods to retrieve a document from a specified URL using Jsoup.
 */
public class JsoupGetter {
    /**
     * The timeout duration for the connection.
     */
    public static final long TIMEOUT = 10_000;

    /**
     * An empty document to return when the requested document is not found.
     */
    public static final Document EMPTY_DOCUMENT = new Document("empty");

    /**
     * Retrieves a document from a specified URL and caches it.
     * If the document is already in the cache, it is returned from there.
     * If the connection times out or too many requests are made, the method waits for a specified timeout duration and tries again.
     * If the requested document is not found, an empty document is returned.
     * @param url The URL to retrieve the document from.
     * @param cache The cache to store the retrieved document in.
     * @return The retrieved document.
     * @throws RuntimeException If an IOException occurs or if the status code of the HTTP response is not 429, 503, or 404.
     */
    @NotNull
    public static Document getDocument(String url, ConcurrentHashMap<String, Document> cache) {
        try {
            if (cache.containsKey(url)) {
                return cache.get(url);
            }
            Document document = Jsoup.connect(url).ignoreContentType(true).get();
            cache.putIfAbsent(url, document);
            return document;
        }
        catch (SocketTimeoutException | SocketException e) {
            //System.err.println("Read timed out for: " + url);
            waitSomeTime();
            return getDocument(url, cache);
        }
        catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            if (statusCode == 429 || statusCode == 503) {
                //System.err.println("Too many requests for: " + url);
                waitSomeTime();
                return getDocument(url, cache);
            }
            else if (statusCode == 404) {
                cache.putIfAbsent(url, EMPTY_DOCUMENT);
                return EMPTY_DOCUMENT;
            }
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Causes the current thread to sleep for the specified timeout duration.
     * @throws RuntimeException If the thread is interrupted while sleeping.
     */
    private static void waitSomeTime() {
        try {
            Thread.sleep(TIMEOUT);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
