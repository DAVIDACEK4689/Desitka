package cz.cuni.mff.desitka.server.communication;

/**
 * This interface represents a listener for communication events.
 */
public interface CommunicationListener {
    /**
     * Called when data is received.
     *
     * @param data the received data
     */
    void onDataReceived(String data);
}