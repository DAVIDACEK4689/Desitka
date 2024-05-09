package cz.cuni.mff.desitka.client.fragments.starting;

/**
 * The StartingStates interface represents the different states of the starting process.
 */
public interface StartingStates {
    /**
     * The StartingState enum represents the different pages in the starting process.
     */
    enum StartingState {
        /**
         * The welcome page state.
         */
        WelcomePage,

        /**
         * The friend game page state.
         */
        FriendGamePage,

        /**
         * The friend game creation page state.
         */
        FriendGameCreate,

        /**
         * The set name page state.
         */
        SetName,

        /**
         * The settings page state.
         */
        Settings,

        /**
         * The join game page state.
         */
        JoinGame,
    }
}