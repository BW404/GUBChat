import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import javax.swing.text.html.*;

public class ChatWindow extends JFrame {
    private JTextPane messageArea;
    private JTextField writeMessageField;
    private ChatClient chatClient;
    private String username;
    private String targetUser;
    private Color myMessageColor = new Color(0x168AFF);
    private Color otherMessageColor = new Color(0xFF6070);
    private JLabel connectionStatus;
    private JLabel typingLabel;

    public ChatWindow(String username, String targetUser, ChatClient chatClient) {
        this.username = username;
        this.targetUser = targetUser;
        this.chatClient = chatClient;
        initializeUI();
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

    private void initializeUI() {
        setTitle("Chat with " + targetUser);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(new Color(0x1C1D22));
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setPreferredSize(new Dimension(getWidth(), 60));
        chatHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        // User info panel (left side of header)
        JPanel userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setOpaque(false);

        JLabel chatHeaderLabel = new JLabel(targetUser);
        chatHeaderLabel.setForeground(Color.WHITE);
        chatHeaderLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        userInfoPanel.add(chatHeaderLabel, BorderLayout.NORTH);

        typingLabel = new JLabel(" ");
        typingLabel.setForeground(Color.GRAY);
        typingLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        userInfoPanel.add(typingLabel, BorderLayout.SOUTH);

        chatHeader.add(userInfoPanel, BorderLayout.WEST);

        // Connection Status
        connectionStatus = new JLabel("Connected");
        connectionStatus.setForeground(Color.GREEN);
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        chatHeader.add(connectionStatus, BorderLayout.EAST);

        add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0x141416));
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Initialize with empty content
        try {
            HTMLDocument doc = (HTMLDocument) messageArea.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit) messageArea.getEditorKit();
            kit.insertHTML(doc, doc.getLength(), "<html><body style='color: white;'></body></html>", 0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }

        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(messageScrollPane, BorderLayout.CENTER);

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

        add(messageInputPanel, BorderLayout.SOUTH);

        // Add window listener to handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                ChatManager.getInstance().closeChatWindow(targetUser);
            }
        });
    }

    private void sendMessage() {
        String message = writeMessageField.getText().trim();
        if (!message.isEmpty() && chatClient != null && chatClient.isConnected()) {
            chatClient.sendMessage(targetUser + " " + message);
            appendMessage("You", message, true, myMessageColor);
            writeMessageField.setText("");
        }
    }

    public void receiveMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> 
            appendMessage(sender, message, false, otherMessageColor)
        );
    }

    public void receiveFile(FileWrapper file) {
        SwingUtilities.invokeLater(() -> {
            appendMessage("System", "Received file: " + file.getFilename(), false, new Color(0x808080));
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

    public void updateConnectionStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            connectionStatus.setText(connected ? "Connected" : "Disconnected");
            connectionStatus.setForeground(connected ? Color.GREEN : Color.RED);
        });
    }
}
