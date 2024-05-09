package cz.cuni.mff.desitka.server.communication;

import cz.cuni.mff.desitka.JSON.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class handles the communication between the server and the client.
 */
public class CommunicationService {
    private final Socket client;
    private final Deque<String> sendMessages = new ConcurrentLinkedDeque<>();
    private CommunicationListener listener;
    private volatile boolean serviceOpen;
    private volatile boolean messageExpected;
    private volatile long messageTimeout;

    /**
     * Constructs a new communication service with a specified client socket.
     *
     * @param client the client socket
     */
    public CommunicationService(Socket client) {
        this.client = client;
    }

    /**
     * Sets the listener for the communication service.
     *
     * @param listener the listener to be set
     */
    public void setListener(CommunicationListener listener) {
        this.listener = listener;
    }

    /**
     * Starts the communication service.
     */
    public void start() {
        serviceOpen = true;
        Thread communicationThread = new Thread(new CommunicationThread());
        communicationThread.start();
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message to be sent
     */
    public void sendMessage(String message) {
        sendMessages.addFirst(message);
    }

    /**
     * Expects a message from the client with a specified timeout.
     *
     * @param timeout the timeout for the message
     */
    public void expectMessage(long timeout) {
        messageTimeout = System.currentTimeMillis() + Constants.EXTRA_TIME + timeout;
        messageExpected = true;

        if (!serviceOpen) {
            listener.onDataReceived("Connection error");
        }
    }

    /**
     * Confirms the receipt of a message from the client.
     */
    public void confirmMessageReceive() {
        messageExpected = false;
    }

    /**
     * Closes the communication service.
     */
    public void close() {
        serviceOpen = false;
    }

    /**
     * This class represents a communication thread for the communication service.
     */
    private class CommunicationThread implements Runnable {

        /**
         * Runs the communication thread.
         */
        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter printer = new PrintWriter(client.getOutputStream(), true)) {

                while (serviceOpen) {
                    if (messageExpected) {
                        checkMessageTimeout();
                        getMessages(reader);
                    }
                    sendMessages(printer);
                    Thread.yield();
                }
            }
            catch (IOException e) {
                if (messageExpected) {
                    listener.onDataReceived("Connection error");
                }
                close();
            }
        }

        /**
         * Checks if the message timeout has been reached.
         *
         * @throws IOException if the message timeout has been reached
         */
        private void checkMessageTimeout() throws IOException {
            if (messageTimeout < System.currentTimeMillis()) {
                throw new IOException("Message timeout");
            }
        }

        /**
         * Gets messages from the client.
         *
         * @param reader the reader to read the messages
         * @throws IOException if an I/O error occurs
         */
        private void getMessages(BufferedReader reader) throws IOException {
            if (reader.ready()) {
                String message = reader.readLine();
                System.out.println("Received message: " + message);
                listener.onDataReceived(message);
            }
        }

        /**
         * Sends messages to the client.
         *
         * @param printer the printer to send the messages
         */
        private void sendMessages(PrintWriter printer) {
            while (!sendMessages.isEmpty()) {
                System.out.println("Sending message: " + sendMessages.peekLast());
                printer.println(sendMessages.removeLast());
            }
        }
    }
}