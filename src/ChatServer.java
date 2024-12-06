import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5000;
    private static Map<String, ClientHandler> clientHandlers = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addClient(String username, ClientHandler handler) {
        clientHandlers.put(username, handler);
        broadcastActiveClients();
    }

    public static synchronized void removeClient(String username) {
        clientHandlers.remove(username);
        broadcastActiveClients();
    }

    public static synchronized ClientHandler getClientHandler(String username) {
        return clientHandlers.get(username);
    }

    private static synchronized void broadcastMessage(String message) {
        for (ClientHandler handler : clientHandlers.values()) {
            try {
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized void broadcastActiveClients() {
        String activeClients = "ACTIVE_CLIENTS:" + String.join(",", clientHandlers.keySet());
        for (ClientHandler handler : clientHandlers.values()) {
            try {
                handler.sendMessage(activeClients);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                // Register user
                // out.writeObject("Enter your username:");
                username = (String) in.readObject();
                ChatServer.addClient(username, this);
                System.out.println(username + " has joined.");
                out.writeObject("Welcome " + username + "! ");

                Object message;
                while ((message = in.readObject()) != null) {
                    if (message instanceof String) {
                        String textMessage = (String) message;
                        if (textMessage.startsWith("PUBLIC_GROUP: ")) {
                            // Handle public group message
                            String publicMessage = textMessage.substring("PUBLIC_GROUP: ".length());
                            broadcastMessage("PUBLIC_GROUP " + username + ": " + publicMessage);
                        } else {
                            // Check if message starts with a username for private messaging
                            String[] parts = textMessage.split(" ", 2);
                            if (parts.length > 1 && clientHandlers.containsKey(parts[0])) {
                                handlePrivateMessage(parts[0] + " " + parts[1]);
                            }
                        }
                    } else if (message instanceof FileWrapper) {
                        handleFile((FileWrapper) message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection with " + username + " lost.");
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

        private void handlePrivateMessage(String textMessage) throws IOException {
            String[] parts = textMessage.split(" ", 2);
            if (parts.length == 2) {
                String targetUsername = parts[0];
                String privateMessage = parts[1];
                ClientHandler targetHandler = ChatServer.getClientHandler(targetUsername);
                if (targetHandler != null) {
                    targetHandler.sendMessage(username + ": " + privateMessage);
                } else {
                    out.writeObject("User " + targetUsername + " is not online.");
                }
            }
        }

        private void handleFile(FileWrapper fileWrapper) throws IOException {
            ClientHandler targetHandler = ChatServer.getClientHandler(fileWrapper.getRecipient());
            if (targetHandler != null) {
                targetHandler.sendFile(fileWrapper);
            } else {
                out.writeObject("User " + fileWrapper.getRecipient() + " is not online.");
            }
        }

        public void sendMessage(String message) throws IOException {
            out.writeObject(message);
        }

        public void sendFile(FileWrapper fileWrapper) throws IOException {
            out.writeObject(fileWrapper);
        }
    }
}
