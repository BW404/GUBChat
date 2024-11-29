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
    private Color myMessageColor = new Color(0x128C7E);  // Updated to match theme
    private Color otherMessageColor = new Color(0x2C2D32);  // Updated to match theme
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
        getContentPane().setBackground(new Color(0x1C1D22));
        getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0x128C7E)));

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0x26272D));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(0x128C7E)));

        // Contact Header
        JPanel contactHeader = new JPanel(new BorderLayout());
        contactHeader.setBackground(new Color(0x26272D));
        contactHeader.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel contactsLabel = new JLabel("Active Users");
        contactsLabel.setForeground(Color.WHITE);
        contactsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        contactHeader.add(contactsLabel, BorderLayout.CENTER);

        // Connection Status
        connectionStatus = new JLabel("Connecting...");
        connectionStatus.setForeground(Color.YELLOW);
        connectionStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        contactHeader.add(connectionStatus, BorderLayout.EAST);

        leftPanel.add(contactHeader, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0x128C7E));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactList.setCellRenderer(new CustomListCellRenderer());
        
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
        contactScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contactScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Right Section: Chat Area
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(0x1C1D22));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(new Color(0x26272D));
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setPreferredSize(new Dimension(getWidth(), 60));
        chatHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x128C7E)));

        selectedUserLabel = new JLabel("Select a user to start chatting");
        selectedUserLabel.setForeground(Color.WHITE);
        selectedUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedUserLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        chatHeader.add(selectedUserLabel, BorderLayout.CENTER);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0x1C1D22));
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messageScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

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
                    chatClient.sendFile(selectedUser, selectedFile.getAbsolutePath());
                    progressBar.setValue(100);
                    progressBar.setString("File sent successfully!");
                    appendMessage(selectedUser, "You", "Sent file: " + selectedFile.getName(), true);
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
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to chat with", 
                "No user selected", JOptionPane.WARNING_MESSAGE);
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
        StringBuilder history = messageHistory.computeIfAbsent(chatUser, k -> new StringBuilder());
        String htmlMessage = formatMessage(sender, message, isRight);
        history.append(htmlMessage);

        if (chatUser.equals(selectedUser)) {
            updateMessageArea(chatUser);
        }
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

    private void updateMessageArea(String user) {
        StringBuilder history = messageHistory.get(user);
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
            connectionStatus.setForeground(connected ? new Color(0x4CAF50) : new Color(0xFF5252));
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
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, 
                    index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C2D32)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
            ));
            return label;
        }
    }

    class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(0x128C7E);
            this.trackColor = new Color(0x2C2D32);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width, thumbBounds.height,
                    8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y,
                    trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}
