package desitka.server;

import java.io.IOException;

/**
 * The main class to run the server.
 */
public class Main {

    /**
     * Main method to run the server.
     * Creates an instance of {@link Server} and calls its {@link Server#start(int, String)} method.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start(4444, "question");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}