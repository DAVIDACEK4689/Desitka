package cz.cuni.mff.desitka.interfaces;

/**
 * Represents the starting states of the game.
 * This interface holds the enumeration of possible starting states.
 */
public interface StartingStates {
    /**
     * Enumeration of possible starting states.
     */
    enum StartingState {
        /**
         * Represents the welcome page state.
         */
        WelcomePage,

        /**
         * Represents the friend game page state.
         */
        FriendGamePage,
    }
}