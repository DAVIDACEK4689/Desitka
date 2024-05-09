package cz.cuni.mff.desitka.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * This class represents the server of the game.
 * It listens for incoming connections and adds them to the game manager.
 */
public class Server {
    /**
     * The port on which the server is listening.
     */
    private final static int LISTENING_PORT = 4444;

    /**
     * The main method of the server.
     * It creates a server socket, binds it to the specified address and port,
     * and then continuously accepts new connections and adds them to the game manager.
     *
     * @param args the command line arguments. This parameter is not used.
     */
    public static void main(String[] args) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", LISTENING_PORT);
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(inetSocketAddress);
            GameManager gameManager = new GameManager();

            while (true) {
                gameManager.addPlayer(serverSocket.accept());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}