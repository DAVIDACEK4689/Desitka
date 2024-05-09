package cz.cuni.mff.desitka.logic.browsing;

import static cz.cuni.mff.desitka.interfaces.BrowsingStates.BrowsingState.BrowsingAnswer;
import static cz.cuni.mff.desitka.interfaces.BrowsingStates.BrowsingState.BrowsingQuestion;
import static cz.cuni.mff.desitka.interfaces.BrowsingStates.BrowsingState.BrowsingStart;

import java.util.HashMap;
import cz.cuni.mff.desitka.interfaces.BrowsingStates;

/**
 * Constants for browsing logic.
 * This class provides a mapping between browsing states and their corresponding scene times.
 */
public class BrowsingConstants implements BrowsingStates {
    private final static HashMap<BrowsingState, Long> timeMap = new HashMap<BrowsingState, Long>() {
        {
            put(BrowsingStart, 0L);
            put(BrowsingAnswer, 3_000L);

            // infinite time for the following states
            put(BrowsingQuestion, Long.MAX_VALUE);
        }
    };

    /**
     * Returns the scene time for a given browsing state.
     * @param browsingState The browsing state.
     * @return The scene time for the given browsing state.
     */
    public static long getSceneTime(BrowsingState browsingState) {
        return timeMap.get(browsingState);
    }
}