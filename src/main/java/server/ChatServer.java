package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Accepts client connections, creates a ClientHandler for each client and manages a list of connected clients.
 */
public class ChatServer {

    private List<ClientHandler> clients;

    /**
     * <div>Initializes the server by accepting incoming client connections on the specified port.</div>
     * <div>For each client connection, it creates a ClientHandler and adds it to the clients list.</div>
     * @param port Specified port number to accept incoming client connections
     */
    public ChatServer(int port) {
        clients = new CopyOnWriteArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Started chat server on port " + port);

            while (true) {
                Socket connectionToClient = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, connectionToClient);
                clients.add(client);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The entry point of the program.
     * It creates an instance of ChatServer and starts the server on port 2710.
     * @param args Arguments
     */
    public static void main(String[] args) {
        new ChatServer(2710);
    }

    /**
     * Sends the message to all connected clients by iterating through the list of clients and calling
     * sendMessage method of each client.
     * @param message Message
     */
    public void broadcastMessage(String message) {
        if (message != null) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Removes the client from the list of connected clients when disconnecting.
     * @param client To be disconnected client.
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
