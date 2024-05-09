package cz.cuni.mff.desitka.JSON;

import com.google.gson.Gson;

/**
 * The GsonParser class provides methods for converting JSON strings to Java objects and vice versa.
 */
public class GsonParser {
    private static final Gson gson = new Gson();

    /**
     * Converts a JSON string to a Java object.
     *
     * @param json The JSON string to convert.
     * @param type The type of the Java object.
     * @param <T> The type parameter that extends JSON.
     * @return The converted Java object.
     */
    public static <T extends JSON> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * Converts a Java object to a JSON string.
     *
     * @param json The Java object to convert.
     * @return The converted JSON string.
     */
    public static String toJson(JSON json) {
        return gson.toJson(json);
    }
}