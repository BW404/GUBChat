import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatWindow extends JFrame implements ChatClient.MessageListener {
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextPane messageArea;
    private JTextField writeMessageField;
    private ChatClient chatClient;
    private String username;
    private String targetUser;
    private Color myMessageColor = new Color(0x128C7E);  // Updated to match theme
    private Color otherMessageColor = new Color(0x2C2D32);  // Updated to match theme
    private JLabel connectionStatus;
    private JLabel selectedUserLabel;
    private Map<String, StringBuilder> messageHistory;
    private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB limit

    public ChatWindow(String username, String targetUser, ChatClient chatClient) {
        this.username = username;
        this.targetUser = targetUser;
        this.chatClient = chatClient;
        this.messageHistory = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chat with " + targetUser);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0x1C1D22));
        getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0x128C7E)));

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(new Color(0x26272D));
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setPreferredSize(new Dimension(getWidth(), 60));
        chatHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x128C7E)));

        selectedUserLabel = new JLabel(targetUser);
        selectedUserLabel.setForeground(Color.WHITE);
        selectedUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedUserLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        chatHeader.add(selectedUserLabel, BorderLayout.CENTER);

        // Connection Status
        connectionStatus = new JLabel("Connected");
        connectionStatus.setForeground(new Color(0x4CAF50));
        connectionStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionStatus.setBorder(new EmptyBorder(0, 0, 0, 20));
        chatHeader.add(connectionStatus, BorderLayout.EAST);

        add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0x1C1D22));
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messageScrollPane.getVerticalScrollBar().setBackground(new Color(0x2C2D32));
        add(messageScrollPane, BorderLayout.CENTER);

        // Message Input Panel
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout(10, 0));
        messageInputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        messageInputPanel.setBackground(new Color(0x26272D));

        // Message Input Field
        writeMessageField = new JTextField();
        writeMessageField.setPreferredSize(new Dimension(200, 40));
        writeMessageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x128C7E), 1),
            new EmptyBorder(5, 15, 5, 15)));
        writeMessageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        writeMessageField.setBackground(new Color(0x2C2D32));
        writeMessageField.setForeground(Color.WHITE);
        writeMessageField.setCaretColor(Color.WHITE);
        messageInputPanel.add(writeMessageField, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(new Color(0x26272D));

        // Attach Button
        JButton attachButton = new JButton("📎");
        attachButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        attachButton.setPreferredSize(new Dimension(40, 40));
        attachButton.setBackground(new Color(0x128C7E));
        attachButton.setForeground(Color.WHITE);
        attachButton.setBorder(BorderFactory.createEmptyBorder());
        attachButton.setFocusPainted(false);
        attachButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        attachButton.addActionListener(e -> selectAndSendFile());
        buttonsPanel.add(attachButton);

        // Send Button
        JButton sendButton = new JButton(new ImageIcon("src/img/send.png"));
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setBackground(new Color(0x128C7E));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder());
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());
        buttonsPanel.add(sendButton);

        messageInputPanel.add(buttonsPanel, BorderLayout.EAST);

        // Add enter key listener to write message field
        writeMessageField.addActionListener(e -> sendMessage());

        add(messageInputPanel, BorderLayout.SOUTH);

        // Handle window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ChatManager.getInstance().closeChatWindow(targetUser);
            }
        });
    }

    private void selectAndSendFile() {
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
            progressDialog.setBackground(new Color(0x1C1D22));
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setString("Preparing to send file...");
            progressBar.setForeground(new Color(0x128C7E));
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
        String message = writeMessageField.getText().trim();
        if (!message.isEmpty() && chatClient != null && chatClient.isConnected()) {
            chatClient.sendMessage(targetUser + " " + message);
            appendMessage("You", message, true);
            writeMessageField.setText("");
        }
    }

    public void receiveMessage(String sender, String message) {
        appendMessage(sender, message, false);
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
        });
    }

    private void appendMessage(String sender, String message, boolean isRight) {
        StringBuilder history = messageHistory.computeIfAbsent(targetUser, k -> new StringBuilder());
        String htmlMessage = formatMessage(sender, message, isRight);
        history.append(htmlMessage);
        updateMessageArea();
    }

    private String formatMessage(String sender, String message, boolean isRight) {
        String alignment = isRight ? "right" : "left";
        Color backgroundColor = isRight ? myMessageColor : otherMessageColor;
        String colorHex = String.format("#%02x%02x%02x", 
            backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
        
        return String.format(
            "<div style='text-align: %s; margin: 10px 0;'>" +
            "<div style='display: inline-block; max-width: 70%%; background-color: %s; " +
            "padding: 10px 15px; border-radius: 15px; margin: 0 20px;'>" +
            "<span style='color: #ffffff; font-family: \"Segoe UI\";'>" +
            "<b>%s</b><br>%s</span></div></div>",
            alignment, colorHex, sender, message
        );
    }

    private void updateMessageArea() {
        StringBuilder history = messageHistory.get(targetUser);
        String content = history != null ? history.toString() : "";
        
        try {
            messageArea.setContentType("text/html");
            messageArea.setText("<html><body style='color: white; font-family: \"Segoe UI\";'>" + 
                              content + "</body></html>");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateConnectionStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setText(connected ? "Connected" : "Disconnected");
            connectionStatus.setForeground(connected ? new Color(0x4CAF50) : new Color(0xFF5252));
        });
    }

    // Required by ChatClient.MessageListener interface but not used in this implementation
    @Override
    public void onMessageReceived(String message) {}

    @Override
    public void onFileReceived(FileWrapper file) {}

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        updateConnectionStatus(connected);
    }
}
