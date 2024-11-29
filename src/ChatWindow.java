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
    private String selectedUser;
    private Color myMessageColor = new Color(0x168AFF);
    private Color otherMessageColor = new Color(0xFF6070);
    private JLabel connectionStatus;
    private JLabel selectedUserLabel;
    private Map<String, StringBuilder> messageHistory;
    private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB limit

    public ChatWindow(String username) {
        this.username = username;
        this.messageHistory = new HashMap<>();
        initializeUI();
        initializeClient();
    }

    private void initializeClient() {
        chatClient = new ChatClient(username, this);
        chatClient.connect();
    }

    private void initializeUI() {
        setTitle("GUB Chat - " + username);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0x26272D));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Contact Header
        JPanel contactHeader = new JPanel(new BorderLayout());
        contactHeader.setBackground(new Color(0x26272D));
        contactHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel contactsLabel = new JLabel("Active Users");
        contactsLabel.setForeground(Color.WHITE);
        contactsLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        contactHeader.add(contactsLabel, BorderLayout.CENTER);

        // Connection Status
        connectionStatus = new JLabel("Connecting...");
        connectionStatus.setForeground(Color.YELLOW);
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        contactHeader.add(connectionStatus, BorderLayout.EAST);

        leftPanel.add(contactHeader, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0x168AFF));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Roboto", Font.PLAIN, 14));
        contactList.setCellRenderer(new CustomListCellRenderer());
        
        // Add selection listener
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String newSelectedUser = contactList.getSelectedValue();
                if (newSelectedUser != null) {
                    selectedUser = newSelectedUser;
                    selectedUserLabel.setText(selectedUser);
                    updateMessageArea(selectedUser);
                }
            }
        });

        JScrollPane contactScrollPane = new JScrollPane(contactList);
        contactScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Right Section: Chat Area
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(0x1C1D22));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(new Color(0x1C1D22));
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setPreferredSize(new Dimension(getWidth(), 60));
        chatHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        selectedUserLabel = new JLabel("Select a user to start chatting");
        selectedUserLabel.setForeground(Color.WHITE);
        selectedUserLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        chatHeader.add(selectedUserLabel, BorderLayout.CENTER);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0x141416));
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Message Input Field
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        messageInputPanel.setBackground(new Color(0x26272D));

        writeMessageField = new JTextField();
        writeMessageField.setPreferredSize(new Dimension(200, 30));
        writeMessageField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        writeMessageField.setFont(new Font("Arial", Font.PLAIN, 14));
        writeMessageField.setBackground(new Color(0xE0E0E0));
        writeMessageField.setForeground(Color.BLACK);
        messageInputPanel.add(writeMessageField, BorderLayout.CENTER);

        // Add file attachment button
        JButton attachButton = new JButton("📎");
        attachButton.setBackground(new Color(0x128C7E));
        attachButton.setForeground(Color.WHITE);
        attachButton.setFont(new Font("Arial", Font.BOLD, 16));
        attachButton.setToolTipText("Attach File");
        attachButton.addActionListener(e -> selectAndSendFile());
        messageInputPanel.add(attachButton, BorderLayout.WEST);

        JButton sendButton = new JButton(new ImageIcon("src/img/send.png"));
        sendButton.setBackground(new Color(0x128C7E));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.addActionListener(e -> sendMessage());
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        // Add enter key listener to write message field
        writeMessageField.addActionListener(e -> sendMessage());

        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void selectAndSendFile() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to send the file to", 
                "No user selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Check file size
            if (selectedFile.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this, 
                    "File size exceeds limit of 100MB", 
                    "File too large", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show progress dialog
            JDialog progressDialog = new JDialog(this, "Sending File", true);
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setString("Preparing to send file...");
            progressDialog.add(progressBar);
            progressDialog.setSize(300, 75);
            progressDialog.setLocationRelativeTo(this);

            // Start file transfer in background
            new Thread(() -> {
                try {
                    progressBar.setString("Sending file...");
                    progressBar.setValue(50);
                    chatClient.sendFile(selectedUser, selectedFile.getAbsolutePath());
                    progressBar.setValue(100);
                    progressBar.setString("File sent successfully!");
                    appendMessage(selectedUser, "You", "Sent file: " + selectedFile.getName(), true);
                    Thread.sleep(1000); // Show completion for 1 second
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
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to chat with", "No user selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String message = writeMessageField.getText().trim();
        if (!message.isEmpty() && chatClient != null && chatClient.isConnected()) {
            chatClient.sendMessage(selectedUser + " " + message);
            appendMessage(selectedUser, "You", message, true);
            writeMessageField.setText("");
        }
    }

    private void appendMessage(String chatUser, String sender, String message, boolean isRight) {
        // Store message in history
        StringBuilder history = messageHistory.computeIfAbsent(chatUser, k -> new StringBuilder());
        String htmlMessage = formatMessage(sender, message, isRight);
        history.append(htmlMessage);

        // Update message area if this is the selected chat
        if (chatUser.equals(selectedUser)) {
            updateMessageArea(chatUser);
        }
    }

    private String formatMessage(String sender, String message, boolean isRight) {
        String alignment = isRight ? "right" : "left";
        Color backgroundColor = isRight ? myMessageColor : otherMessageColor;
        String colorHex = String.format("#%02x%02x%02x", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
        String paddingLeft = isRight ? "margin-left: 100px;" : "margin-right: 100px;";
        
        return String.format(
            "<div style='text-align: %s; margin: 5px; border-radius: 10px;'>"
            + "<p style='background-color: %s; color: white; padding: 5px 15px; display: inline-block; max-width: 50%%; margin: auto; border-radius: 15px; %s'>"
            + "<b>%s:</b> %s</p></div>",
            alignment, colorHex, paddingLeft, sender, message
        );
    }

    private void updateMessageArea(String user) {
        StringBuilder history = messageHistory.get(user);
        String content = history != null ? history.toString() : "";
        
        try {
            messageArea.setContentType("text/html");
            messageArea.setText("<html><body style='color: white;'>" + content + "</body></html>");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
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
                appendMessage(sender, sender, content, false);
            }
        });
    }

    @Override
    public void onFileReceived(FileWrapper file) {
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
                    appendMessage(file.getRecipient(), "System", 
                        "Received file: " + file.getFilename() + "\nSaved as: " + saveFile.getName(), 
                        false);
                } catch (IOException e) {
                    appendMessage(file.getRecipient(), "System", 
                        "Error saving file: " + e.getMessage(), 
                        false);
                }
            }
        });
    }

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setText(connected ? "Connected" : "Disconnected");
            connectionStatus.setForeground(connected ? Color.GREEN : Color.RED);
        });
    }

    private void updateContactList(String[] clients) {
        SwingUtilities.invokeLater(() -> {
            contactListModel.clear();
            for (String client : clients) {
                if (!client.equals(username)) {
                    contactListModel.addElement(client);
                }
            }
        });
    }

    class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C2D32)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            return label;
        }
    }
}
