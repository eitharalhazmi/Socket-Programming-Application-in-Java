// Eithar Alhazmi
// 2105616

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ITServer {

    private static final int SERVER_PORT = 1248;

    // List to store client handlers for connected clients
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is running and listening for client connections on port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming client connections
                System.out.println("Accepted a new client connection: " + clientSocket);

                // Create a client handler for the new client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                // Start a new thread to handle the client
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle each connected client
    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final PrintWriter out;
        private final BufferedReader in;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String clientAddress = clientSocket.getRemoteSocketAddress().toString();
                System.out.println("Handling client: " + clientAddress);

                String username = in.readLine(); // Read the username from the client
                if (username == null) {
                    return; // Invalid client without a username
                }

                System.out.println("Client " + username + " connected from: " + clientAddress);

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break; // Client disconnected
                    }
                    
                    // broadcasts messages to all connected clients:
                    broadcastMessage(username, message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        private void broadcastMessage(String sender, String message) {
            System.out.println("Broadcasting message from " + sender + ": " + message);
            for (ClientHandler client : clients) {
                client.sendMessage(sender + ": " + message);
            }
        }

        private void sendMessage(String message) {
            out.println(message);
        }

        private void close() {
            try {
                in.close();
                out.close();
                clientSocket.close();
                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
