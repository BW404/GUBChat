import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5001;
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
    }

    public static synchronized void removeClient(String username) {
        clientHandlers.remove(username);
    }

    public static synchronized ClientHandler getClientHandler(String username) {
        return clientHandlers.get(username);
    }

    public static synchronized List<String> getConnectedClients() {
        return new ArrayList<>(clientHandlers.keySet());
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
                out.writeObject("Enter your username:");
                username = (String) in.readObject();
                ChatServer.addClient(username, this);
                System.out.println(username + " has joined.");
                out.writeObject("Welcome " + username + "! Use '@username message' to chat privately.");

                Object message;
                while ((message = in.readObject()) != null) {
                    if (message instanceof String) {
                        String textMessage = (String) message;
                        if (textMessage.startsWith("@")) {
                            handlePrivateMessage(textMessage);
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
            int spaceIndex = textMessage.indexOf(" ");
            if (spaceIndex > 0) {
                String targetUsername = textMessage.substring(1, spaceIndex);
                String privateMessage = textMessage.substring(spaceIndex + 1);
                ClientHandler targetHandler = ChatServer.getClientHandler(targetUsername);
                if (targetHandler != null) {
                    targetHandler.sendMessage("Private from " + username + ": " + privateMessage);
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
