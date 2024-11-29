import java.util.HashMap;
import java.util.Map;

public class ChatManager {
    private static ChatManager instance;
    private Map<String, ChatWindow> chatWindows;
    private ChatClient chatClient;
    private String currentUser;

    private ChatManager() {
        chatWindows = new HashMap<>();
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void initialize(String username) {
        this.currentUser = username;
        this.chatClient = new ChatClient(username, new MainMessageListener());
        this.chatClient.connect();
    }

    public void openChatWindow(String targetUser) {
        if (!chatWindows.containsKey(targetUser)) {
            ChatWindow chatWindow = new ChatWindow(currentUser, targetUser, chatClient);
            chatWindows.put(targetUser, chatWindow);
            chatWindow.setVisible(true);
        } else {
            ChatWindow existingWindow = chatWindows.get(targetUser);
            existingWindow.setVisible(true);
            existingWindow.toFront();
            existingWindow.requestFocus();
        }
    }

    private class MainMessageListener implements ChatClient.MessageListener {
        @Override
        public void onMessageReceived(String message) {
            if (message.startsWith("ACTIVE_CLIENTS:")) {
                // Update all chat windows with the new client list
                for (ChatWindow window : chatWindows.values()) {
                    window.updateContactList(message.substring("ACTIVE_CLIENTS:".length()).split(","));
                }
            } else if (message.contains(":")) {
                // Handle private messages
                String[] parts = message.split(":", 2);
                String sender = parts[0].trim();
                String content = parts[1].trim();
                
                // Open chat window if it doesn't exist
                if (!chatWindows.containsKey(sender)) {
                    openChatWindow(sender);
                }
                
                // Route message to appropriate chat window
                ChatWindow targetWindow = chatWindows.get(sender);
                if (targetWindow != null) {
                    targetWindow.receiveMessage(sender, content);
                }
            }
        }

        @Override
        public void onFileReceived(FileWrapper file) {
            String sender = file.getRecipient();
            if (!chatWindows.containsKey(sender)) {
                openChatWindow(sender);
            }
            ChatWindow targetWindow = chatWindows.get(sender);
            if (targetWindow != null) {
                targetWindow.receiveFile(file);
            }
        }

        @Override
        public void onConnectionStatusChanged(boolean connected) {
            for (ChatWindow window : chatWindows.values()) {
                window.updateConnectionStatus(connected);
            }
        }
    }

    public void closeChatWindow(String username) {
        chatWindows.remove(username);
    }

    public ChatClient getChatClient() {
        return chatClient;
    }
}
