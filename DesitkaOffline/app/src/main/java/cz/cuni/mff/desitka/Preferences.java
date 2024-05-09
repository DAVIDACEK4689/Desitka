package cz.cuni.mff.desitka;

import android.content.Context;

/**
 * This class is used to manage the preferences of the application.
 */
public class Preferences {
    private static final String FILE = "preferences";
    private static final String PLAYERS_COUNT = "playersCount";

    /**
     * This method is used to set the number of players.
     * @param context This is the first parameter to setPlayersCount method
     * @param playersCount  This is the second parameter to setPlayersCount method
     */
    public static void setPlayersCount(Context context, int playersCount) {
        android.content.SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PLAYERS_COUNT, playersCount);
        editor.apply();
    }

    /**
     * This method is used to get the number of players.
     * @param context This is the parameter to getPlayersCount method
     * @return int This returns the number of players.
     */
    public static int getPlayersCount(Context context) {
        return getSharedPreferences(context).getInt(PLAYERS_COUNT, 2);
    }

    /**
     * This method is used to get the shared preferences.
     * @param context This is the parameter to getSharedPreferences method
     * @return SharedPreferences This returns the shared preferences.
     */
    private static android.content.SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}