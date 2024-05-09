package cz.cuni.mff.desitka.client.communication;

/**
 * The CommunicationListener interface defines the methods that must be implemented
 * by classes that want to receive communication events.
 */
public interface CommunicationListener {
    /**
     * Starts the game timer. This is a default method and can be overridden by implementing classes.
     */
    default void startGameTimer() {};

    /**
     * Cancels the game timer. This is a default method and can be overridden by implementing classes.
     */
    default void cancelGameTimer() {};

    /**
     * Called when data is received.
     *
     * @param data The received data.
     */
    void onDataReceived(String data);
}