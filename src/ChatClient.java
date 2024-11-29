import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5001;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private MessageListener messageListener;
    private boolean isConnected = false;

    public interface MessageListener {
        void onMessageReceived(String message);
        void onFileReceived(FileWrapper file);
        void onConnectionStatusChanged(boolean connected);
    }

    public ChatClient(String username, MessageListener listener) {
        this.username = username;
        this.messageListener = listener;
    }

    public void connect() { 
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            // Start reading messages in a separate thread
            new Thread(this::readMessages).start();
            
            // Send username to server
            out.writeObject(username);
            isConnected = true;
            messageListener.onConnectionStatusChanged(true);
            
            // Request the list of connected clients
            out.writeObject("GET_CLIENTS");
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            // Start reading messages in a separate thread
            new Thread(this::readMessages).start();
            
            // Send username to server
            out.writeObject(username);
            isConnected = true;
            messageListener.onConnectionStatusChanged(true);
            
        } catch (IOException e) {
            e.printStackTrace();
            messageListener.onConnectionStatusChanged(false);
        }
    }

    private void readMessages() {
        try {
            Object message;
            while ((message = in.readObject()) != null) {
                if (message instanceof String) {
                    messageListener.onMessageReceived((String) message);
                } else if (message instanceof FileWrapper) {
                    messageListener.onFileReceived((FileWrapper) message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            isConnected = false;
            messageListener.onConnectionStatusChanged(false);
        }
    }

    public void sendMessage(String message) {
        try {
            if (isConnected && out != null) {
                out.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
            messageListener.onConnectionStatusChanged(false);
        }
    }

    public void sendFile(String recipient, String filepath) {
        try {
            File file = new File(filepath);
            byte[] content = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(content);
            }
            FileWrapper fileWrapper = new FileWrapper(recipient, file.getName(), content);
            out.writeObject(fileWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            isConnected = false;
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            messageListener.onConnectionStatusChanged(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
