package cz.cuni.mff.desitka.client.sharedPreferences;

import android.content.Context;

import cz.cuni.mff.desitka.JSON.Constants;


/**
 * A utility class for managing shared preferences.
 */
public class Preferences {
    private static final String FILE = "preferences";
    private static final String PLAYER_NAME = "playerName";
    private static final String FRIEND_GAME_PLAYERS = "friendGamePlayers";
    private static final String SERVER_ADDRESS = "serverUrl";
    private static final String SERVER_PORT = "serverPort";

    /**
     * Sets the player's name in shared preferences.
     *
     * @param context the context
     * @param playerName the player's name
     */
    public static void setPlayerName(Context context, String playerName) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PLAYER_NAME, playerName);
        editor.apply();
    }

    /**
     * Retrieves the player's name from shared preferences.
     *
     * @param context the context
     * @return the player's name
     */
    public static String getPlayerName(Context context) {
        return getSharedPreferences(context).getString(PLAYER_NAME, "");
    }

    /**
     * Sets the number of players in a friend game in shared preferences.
     *
     * @param context the context
     * @param playerCount the number of players
     */
    public static void setFriendGamePlayers(Context context, int playerCount) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(FRIEND_GAME_PLAYERS, playerCount);
        editor.apply();
    }

    /**
     * Retrieves the number of players in a friend game from shared preferences.
     *
     * @param context the context
     * @return the number of players
     */
    public static int getFriendGamePlayers(Context context) {
        return getSharedPreferences(context).getInt(FRIEND_GAME_PLAYERS, 0);
    }

    /**
     * Sets the server address in shared preferences.
     *
     * @param context the context
     * @param serverURL the server address
     */
    public static void setServerAddress(Context context, String serverURL) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SERVER_ADDRESS, serverURL);
        editor.apply();
    }

    /**
     * Retrieves the server address from shared preferences.
     *
     * @param context the context
     * @return the server address
     */
    public static String getServerAddress(Context context) {
        return getSharedPreferences(context).getString(SERVER_ADDRESS, Constants.getServerURL());
    }

    /**
     * Sets the server port in shared preferences.
     *
     * @param context the context
     * @param serverPort the server port
     */
    public static void setServerPort(Context context, int serverPort) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SERVER_PORT, serverPort);
        editor.apply();
    }

    /**
     * Retrieves the server port from shared preferences.
     *
     * @param context the context
     * @return the server port
     */
    public static int getServerPort(Context context) {
        return getSharedPreferences(context).getInt(SERVER_PORT, Constants.getServerPort());
    }

    /**
     * Retrieves the shared preferences.
     *
     * @param context the context
     * @return the shared preferences
     */
    private static android.content.SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}