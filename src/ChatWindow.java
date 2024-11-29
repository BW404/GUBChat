import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatWindow extends JFrame implements ChatClient.MessageListener {
    private static final long serialVersionUID = 1L;
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextPane messageArea;
    private JTextField writeMessageField;
    private JTextField searchField;
    private ChatClient chatClient;
    private String username;
    private String targetUser;
    private Color myMessageColor = new Color(0x8DE8E3); // Light blue for sent messages
    private Color otherMessageColor = new Color(0x2C2C2E); // Dark gray for received messages
    private JLabel connectionStatus;
    private JLabel selectedUserLabel;
    private Map<String, StringBuilder> messageHistory;
    private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB limit
    private static final Color DARK_BG = new Color(0x1C1C1E);
    private static final Color DARKER_BG = new Color(0x2C2C2E);
    private static final Color TEXT_COLOR = new Color(0xFFFFFF);
    private boolean isMainWindow;

    // Constructor for main chat window (from login)
    public ChatWindow(String username) {
        this.username = username;
        this.isMainWindow = true;
        this.messageHistory = new HashMap<>();
        ChatManager.getInstance().initialize(username);
        initializeMainUI();
    }

    // Constructor for individual chat windows (from ChatManager)
    public ChatWindow(String username, String targetUser, ChatClient chatClient) {
        this.username = username;
        this.targetUser = targetUser;
        this.chatClient = chatClient;
        this.isMainWindow = false;
        this.messageHistory = new HashMap<>();
        initializeUI();
    }

    private void initializeMainUI() {
        setTitle("GUB Chat - " + username);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setBackground(DARK_BG);

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(DARKER_BG);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(DARKER_BG);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        searchField = new JTextField();
        searchField.setBackground(DARK_BG);
        searchField.setForeground(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BG),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setBackground(DARKER_BG);
        contactList.setForeground(TEXT_COLOR);
        contactList.setSelectionBackground(new Color(0x3A3A3C));
        contactList.setSelectionForeground(TEXT_COLOR);
        contactList.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        contactList.setCellRenderer(new CustomListCellRenderer());
        
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = contactList.getSelectedValue();
                if (selectedUser != null) {
                    ChatManager.getInstance().openChatWindow(selectedUser);
                }
            }
        });

        JScrollPane contactScrollPane = new JScrollPane(contactList);
        contactScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contactScrollPane.setBackground(DARKER_BG);
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);

        // Connection status
        connectionStatus = new JLabel("Connecting...");
        connectionStatus.setForeground(new Color(0x4CD964));
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionStatus.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.add(connectionStatus, BorderLayout.SOUTH);
    }

    private void initializeUI() {
        setTitle("Chat with " + targetUser);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setBackground(DARK_BG);

        // Chat Area
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(DARK_BG);

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(DARKER_BG);
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setBorder(new EmptyBorder(10, 20, 10, 20));

        selectedUserLabel = new JLabel(targetUser);
        selectedUserLabel.setForeground(TEXT_COLOR);
        selectedUserLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        chatHeader.add(selectedUserLabel, BorderLayout.CENTER);

        connectionStatus = new JLabel("Connected");
        connectionStatus.setForeground(new Color(0x4CD964));
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        chatHeader.add(connectionStatus, BorderLayout.EAST);

        chatPanel.add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(DARK_BG);
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        messageArea.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messageScrollPane.setBackground(DARK_BG);
        chatPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Message Input Panel
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout(10, 0));
        messageInputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        messageInputPanel.setBackground(DARKER_BG);

        // Button Panel (Left)
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftButtonPanel.setBackground(DARKER_BG);
        
        JButton emojiButton = new JButton("😊");
        emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        emojiButton.setBorderPainted(false);
        emojiButton.setContentAreaFilled(false);
        emojiButton.setFocusPainted(false);
        emojiButton.setForeground(TEXT_COLOR);
        
        JButton attachButton = new JButton("📎");
        attachButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        attachButton.setBorderPainted(false);
        attachButton.setContentAreaFilled(false);
        attachButton.setFocusPainted(false);
        attachButton.setForeground(TEXT_COLOR);
        attachButton.addActionListener(e -> selectAndSendFile());

        leftButtonPanel.add(emojiButton);
        leftButtonPanel.add(attachButton);

        // Message Input Field
        writeMessageField = new JTextField();
        writeMessageField.setBackground(DARK_BG);
        writeMessageField.setForeground(TEXT_COLOR);
        writeMessageField.setCaretColor(TEXT_COLOR);
        writeMessageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BG),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        writeMessageField.putClientProperty("JTextField.placeholderText", "Write a message...");
        writeMessageField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));

        // Send Button
        JButton sendButton = new JButton("➤");
        sendButton.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        sendButton.setForeground(new Color(0x8DE8E3));
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> sendMessage());

        messageInputPanel.add(leftButtonPanel, BorderLayout.WEST);
        messageInputPanel.add(writeMessageField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        writeMessageField.addActionListener(e -> sendMessage());

        chatPanel.add(messageInputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (!isMainWindow) {
                    ChatManager.getInstance().closeChatWindow(targetUser);
                }
            }
        });
    }

    private void selectAndSendFile() {
        if (isMainWindow) return;

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            if (selectedFile.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this, 
                    "File size exceeds limit of 100MB", 
                    "File too large", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog progressDialog = new JDialog(this, "Sending File", true);
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setString("Preparing to send file...");
            progressDialog.add(progressBar);
            progressDialog.setSize(300, 75);
            progressDialog.setLocationRelativeTo(this);

            new Thread(() -> {
                try {
                    progressBar.setString("Sending file...");
                    progressBar.setValue(50);
                    chatClient.sendFile(targetUser, selectedFile.getAbsolutePath());
                    progressBar.setValue(100);
                    progressBar.setString("File sent successfully!");
                    appendMessage("You", "Sent file: " + selectedFile.getName(), true);
                    Thread.sleep(1000);
                    SwingUtilities.invokeLater(() -> progressDialog.dispose());
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        JOptionPane.showMessageDialog(this, 
                            "Error sending file: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

            progressDialog.setVisible(true);
        }
    }

    private void sendMessage() {
        if (isMainWindow) return;

        String message = writeMessageField.getText().trim();
        if (!message.isEmpty() && chatClient != null && chatClient.isConnected()) {
            chatClient.sendMessage(targetUser + " " + message);
            appendMessage("You", message, true);
            writeMessageField.setText("");
        }
    }

    @Override
    public void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("ACTIVE_CLIENTS:")) {
                String[] clients = message.substring("ACTIVE_CLIENTS:".length()).split(",");
                updateContactList(clients);
            } else if (message.contains(":")) {
                String[] parts = message.split(":", 2);
                String sender = parts[0].trim();
                String content = parts[1].trim();
                appendMessage(sender, content, false);
            }
        });
    }

    public void receiveMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            appendMessage(sender, message, false);
        });
    }

    public void receiveFile(FileWrapper file) {
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(file.getFilename()));
            fileChooser.setDialogTitle("Save Received File");
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                try {
                    try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                        fos.write(file.getContent());
                    }
                    appendMessage("System", 
                        "Received file: " + file.getFilename() + "\nSaved as: " + saveFile.getName(), 
                        false);
                } catch (IOException e) {
                    appendMessage("System", 
                        "Error saving file: " + e.getMessage(), 
                        false);
                }
            }
            e.printStackTrace();
        }
    }

    private String formatMessage(String sender, String message, boolean isRight) {
        String alignment = isRight ? "right" : "left";
        Color backgroundColor = isRight ? myMessageColor : otherMessageColor;
        String colorHex = String.format("#%02x%02x%02x", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
        String textColor = isRight ? "#000000" : "#FFFFFF";
        String marginStyle = isRight ? "margin-left: auto; margin-right: 20px;" : "margin-left: 20px; margin-right: auto;";
        
        return String.format(
            "<div style='display: flex; justify-content: %s; margin: 10px 0;'>" +
            "<div style='background-color: %s; color: %s; padding: 12px 16px; " +
            "border-radius: 18px; max-width: 60%%; %s'>" +
            "<span style='font-weight: %s'>%s</span></div></div>",
            alignment, colorHex, textColor, marginStyle,
            isRight ? "normal" : "bold",
            message
        );
    }

    public void updateConnectionStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setText(connected ? "Connected" : "Disconnected");
            connectionStatus.setForeground(connected ? new Color(0x4CD964) : new Color(0xFF3B30));
        });
    }

    private class CustomListCellRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x3A3A3C)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
            ));
            return label;
        }
    }
}
