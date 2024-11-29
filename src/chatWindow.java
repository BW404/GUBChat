import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import javax.swing.text.html.*;

class ChatBubblePanel extends JPanel {
    private String message;
    private Color backgroundColor;

    public ChatBubblePanel(String message, Color backgroundColor) {
        this.message = message;
        this.backgroundColor = backgroundColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
        g2d.setColor(Color.WHITE);
        g2d.drawString(message, 10, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 50);
    }
}

public class ChatWindow extends JFrame implements ChatClient.MessageListener {
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextPane messageArea;
    private JTextField writeMessageField;
    private ChatClient chatClient;
    private String username;
    private Color myMessageColor = new Color(0x168AFF);
    private Color otherMessageColor = new Color(0xFF6070);
    private JLabel connectionStatus;

    public ChatWindow(String username) {
        this.username = username;
        initializeUI();
        initializeClient();
    }

    // Default constructor for backward compatibility
    public ChatWindow() {
        this("Anonymous");
    }

    private void initializeClient() {
        chatClient = new ChatClient(username, this);
        chatClient.connect();
    }

    private void initializeUI() {
        setTitle("Chat Application - " + username);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0x26272D));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Search Bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(0x26272D));
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBackground(new Color(0xF0F0F0));
        searchField.setForeground(Color.BLACK);
        searchField.setText("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Connection Status
        connectionStatus = new JLabel("Connecting...");
        connectionStatus.setForeground(Color.YELLOW);
        connectionStatus.setHorizontalAlignment(SwingConstants.CENTER);
        searchPanel.add(connectionStatus, BorderLayout.SOUTH);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0xD0D0D0));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Roboto", Font.PLAIN, 14));
        contactList.setCellRenderer(new CustomListCellRenderer());

        JScrollPane contactScrollPane = new JScrollPane(contactList);
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

        JLabel chatHeaderLabel = new JLabel("Chat Room");
        chatHeaderLabel.setForeground(Color.WHITE);
        chatHeaderLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        chatHeader.add(chatHeaderLabel, BorderLayout.CENTER);
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

        // Add window listener to handle disconnection
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (chatClient != null) {
                    chatClient.disconnect();
                }
            }
        });
    }

    private void sendMessage() {
        String message = writeMessageField.getText().trim();
        if (!message.isEmpty() && chatClient != null && chatClient.isConnected()) {
            chatClient.sendMessage(message);
            appendMessage("You", message, true, myMessageColor);
            writeMessageField.setText("");
        }
    }

    private void appendMessage(String sender, String message, boolean isRight, Color backgroundColor) {
        String alignment = isRight ? "right" : "left";
        String colorHex = String.format("#%02x%02x%02x", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
        String paddingLeft = isRight ? "margin-left: 100px;" : "margin-right: 100px;";
        
        String htmlMessage = String.format(
            "<div style='text-align: %s; margin: 5px; border-radius: 10px;'>"
            + "<p style='background-color: %s; color: white; padding: 5px 15px; display: inline-block; max-width: 50%%; margin: auto; border-radius: 15px; %s'>"
            + "<b>%s:</b> %s</p></div>",
            alignment, colorHex, paddingLeft, sender, message
        );  
    
        try {
            HTMLDocument doc = (HTMLDocument) messageArea.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit) messageArea.getEditorKit();
            kit.insertHTML(doc, doc.getLength(), htmlMessage, 0, 0, null);
            
            // Auto-scroll to bottom
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        } catch (BadLocationException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error appending message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ChatClient.MessageListener Implementation
    @Override
    public void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("Welcome") || message.contains("has joined") || message.contains("is not online")) {
                // System message
                appendMessage("System", message, false, new Color(0x808080));
            } else if (message.startsWith("Private from")) {
                // Private message
                appendMessage("Private", message, false, otherMessageColor);
            } else if (message.startsWith("CLIENT_LIST:")) {
                // Update contact list
                String[] clients = message.substring("CLIENT_LIST:".length()).split(",");
                contactListModel.clear();
                for (String client : clients) {
                    contactListModel.addElement(client.trim());
                }
            } else {
                // Regular message
                appendMessage("Other", message, false, otherMessageColor);
            }
        });
    }

    @Override
    public void onFileReceived(FileWrapper file) {
        SwingUtilities.invokeLater(() -> {
            appendMessage("System", "Received file: " + file.getFilename(), false, new Color(0x808080));
            // Handle file saving
            try {
                File savedFile = new File("received_" + file.getFilename());
                try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                    fos.write(file.getContent());
                }
                appendMessage("System", "File saved as: " + savedFile.getName(), false, new Color(0x808080));
            } catch (IOException e) {
                appendMessage("System", "Error saving file: " + e.getMessage(), false, new Color(0xFF0000));
            }
        });
    }

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                connectionStatus.setText("Connected");
                connectionStatus.setForeground(Color.GREEN);
            } else {
                connectionStatus.setText("Disconnected");
                connectionStatus.setForeground(Color.RED);
            }
        });
    }

    // Custom List Cell Renderer
    class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(new Color(0xD0D0D0));
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(new Color(0x2C2D32));
                label.setForeground(Color.WHITE);
            }

            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 5)
            ));
            label.setHorizontalAlignment(SwingConstants.LEFT);

            return label;
        }
    }
}
