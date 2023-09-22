package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    private List<ClientHandler> clients;

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

    public static void main(String[] args) {
        new ChatServer(2710);
    }

    public void broadcastMessage(String message) {
        if (message != null) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
