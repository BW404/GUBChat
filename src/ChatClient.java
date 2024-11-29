import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "jalauddintaj-58689.portmap.io";
    private static final int PORT = 58689;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatWindow chatWindow; // Reference to the ChatWindow

    public ChatClient(ChatWindow chatWindow) {
        this.chatWindow = chatWindow; // Store the reference
        connectToServer();
    }

    private void connectToServer() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            System.out.println("Connected to the chat server.");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Thread readThread = new Thread(() -> {
                try {
                    Object message;
                    while ((message = in.readObject()) != null) {
                        if (message instanceof String) {
                            String receivedMessage = (String) message;
                                chatWindow.appendMessage("Server", receivedMessage); // Update the chat window
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeObject(message); // Send message to the server
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}