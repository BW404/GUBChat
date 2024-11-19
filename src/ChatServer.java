import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 8000;
    private static Map<String, ClientHandler> clientHandlers = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Start a new thread for the connected client
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addClient(String username, ClientHandler handler) {
        clientHandlers.put(username, handler);
    }

    public static synchronized void removeClient(String username) {
        clientHandlers.remove(username);
    }

    public static synchronized ClientHandler getClientHandler(String username) {
        return clientHandlers.get(username);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Register the user
                out.println("Enter your username:");
                username = in.readLine();
                ChatServer.addClient(username, this);

                out.println("Welcome " + username + "! You can start private chats by typing '@username message'.");
                System.out.println(username + " has joined.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("@")) {
                        // Private message
                        int spaceIndex = message.indexOf(" ");
                        if (spaceIndex > 0) {
                            String targetUsername = message.substring(1, spaceIndex);
                            String privateMessage = message.substring(spaceIndex + 1);

                            ClientHandler targetHandler = ChatServer.getClientHandler(targetUsername);
                            if (targetHandler != null) {
                                targetHandler.sendMessage("Private from " + username + ": " + privateMessage);
                            } else {
                                out.println("User " + targetUsername + " is not online.");
                            }
                        }
                    } else if (message.startsWith("/file")) {
                        // File attachment
                        out.println("File transfer is not yet implemented.");
                    } else {
                        out.println("Invalid command. Use '@username message' for private chats.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ChatServer.removeClient(username);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(username + " has disconnected.");
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
