package cz.cuni.mff.desitka.interfaces;

/**
 * Represents the browsing states in the application.
 * This interface holds the enumeration of possible browsing states.
 */
public interface BrowsingStates {

    /**
     * Enumeration of possible browsing states.
     */
    enum BrowsingState {
        /**
         * Represents the start of browsing.
         */
        BrowsingStart,

        /**
         * Represents the state when a question is being browsed.
         */
        BrowsingQuestion,

        /**
         * Represents the state when an answer is being browsed.
         */
        BrowsingAnswer
    }
}