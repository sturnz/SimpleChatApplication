package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ChatServer server;
    private final Socket     connectionToClient;
    private final String     name;
    private BufferedReader   fromClientReader;
    private PrintWriter      toClientWriter;

    /**
     * Constructs a new ClientHandler to manage communication with a connected client.
     *
     * @param server The ChatServer instance managing this client.
     * @param connectionToClient The Socket representing the connection to the client.
     */
    public ClientHandler(ChatServer server, Socket connectionToClient) {
        this.server             = server;
        this.connectionToClient = connectionToClient;
        this.name               = connectionToClient.getInetAddress().getHostName();

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            fromClientReader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
            toClientWriter   = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()));

            server.broadcastMessage(name + " connected.");

            String message = fromClientReader.readLine();

            while (message != null) {
                server.broadcastMessage(name + " : " + message);
                message = fromClientReader.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fromClientReader != null) {
                try {
                    fromClientReader.close();
                } catch (IOException e) {
                    server.removeClient(this);
                    server.broadcastMessage(name + " disconnected.");
                    e.printStackTrace();
                }
            }
            if (toClientWriter != null) {
                toClientWriter.close();
            }
        }
    }

    public void sendMessage(String message) {
        toClientWriter.println(message);
        toClientWriter.flush();
    }
}
