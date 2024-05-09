package cz.cuni.mff.desitka.client.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * The CommunicationService class handles the communication with the server.
 */
public class CommunicationService {
    private final String url;
    private final int port;
    private volatile boolean serviceOpen;
    private final Deque<String> sendMessages = new ConcurrentLinkedDeque<>();
    private CommunicationListener communicationListener;

    /**
     * Constructs a new CommunicationService.
     *
     * @param url The URL of the server.
     * @param port The port of the server.
     */
    public CommunicationService(String url, int port) {
        this.url = url;
        this.port = port;
    }

    /**
     * Sets the CommunicationListener.
     *
     * @param listener The listener to set.
     */
    public void setCommunicationListener(CommunicationListener listener) {
        communicationListener = listener;
    }

    /**
     * Starts the communication service.
     */
    public void start() {
        if (!serviceOpen) {
            serviceOpen = true;
            Thread communicationThread = new Thread(new CommunicationThread());
            communicationThread.start();
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        sendMessages.addFirst(message);
    }

    /**
     * Closes the communication service.
     */
    public void closeService() {
        serviceOpen = false;
    }

    /**
     * The CommunicationThread class handles the communication with the server in a separate thread.
     */
    private class CommunicationThread implements Runnable {

        /**
         * The run method of the thread.
         */
        @Override
        public void run () {
            // Start timer before opening connection
            communicationListener.startGameTimer();

            try (Socket client = new Socket(url, port);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter printer = new PrintWriter(client.getOutputStream(), true)) {

                while (serviceOpen) {
                    while (reader.ready()) {
                        communicationListener.onDataReceived(reader.readLine());
                    }
                    sendMessages(printer);
                    Thread.yield();
                }

                // Cancel timer after closing connection
                communicationListener.cancelGameTimer();
            }
            catch (IOException e) {
                communicationListener.onDataReceived("Connection error");
                communicationListener.cancelGameTimer();
                serviceOpen = false;
            }
        }

        /**
         * Sends all messages in the queue to the server.
         *
         * @param printer The PrintWriter to use for sending the messages.
         */
        private void sendMessages(PrintWriter printer) {
            while (!sendMessages.isEmpty()) {
                printer.println(sendMessages.removeLast());
            }
        }
    }
}