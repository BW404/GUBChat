import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

class ChatBubblePanel extends JPanel {
    private String message;
    private Color backgroundColor;

    public ChatBubblePanel(String message, Color backgroundColor) {
        this.message = message;
        this.backgroundColor = backgroundColor;
        setOpaque(false); // Make the panel transparent
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15)); // Rounded rectangle
        g2d.setColor(Color.WHITE);
        g2d.drawString(message, 10, 20); // Draw the message
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 50); // Set a preferred size for the bubble
    }
}

public class ChatWindow extends JFrame {

    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private JTextPane messageArea;
    private JTextField writeMessageField;

    public ChatWindow() {
        setTitle("Chat Application");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);  // Center the window

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0x084D44)); // Light grey background
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

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactListModel.addElement("John Doe");
        contactListModel.addElement("Jane Smith");
        contactListModel.addElement("Alice Johnson");
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x084D44));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0xD0D0D0));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Roboto", Font.PLAIN, 14));

        // Set a custom cell renderer for the contact list
        contactList.setCellRenderer(new CustomListCellRenderer());

        // Add the contact list to a scroll pane
        JScrollPane contactScrollPane = new JScrollPane(contactList);
        leftPanel.add(contactScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Right Section: Chat Area
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(0x084D44));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chat Header
        JPanel chatHeader = new JPanel();
        chatHeader.setBackground(new Color(0x1C1D22));
        chatHeader.setLayout(new BorderLayout());
        chatHeader.setPreferredSize(new Dimension(getWidth(), 60));
        chatHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel chatHeaderLabel = new JLabel("Chat with John Doe"); // Should get the name from the chat name
        chatHeaderLabel.setForeground(Color.WHITE);
        chatHeaderLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        chatHeader.add(chatHeaderLabel, BorderLayout.CENTER);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(0x141416));
        messageArea.setForeground(Color.WHITE);
        messageArea.setFont(new Font("Lucida Handwriting", Font.PLAIN, 18));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Red and Blue are the colors of the chat bubbles
        Color red = new Color(0xFF6070);
        Color blue = new Color(0x168AFF);

        // Add some dummy chat messages
        appendMessage("John Doe", "Hi there!", false, red); 
        appendMessage("You", "Hello! How are you?", true, blue); 
        appendMessage("John Doe", "I'm good, thanks! How about you?", false, red);
        appendMessage("You", "I'm doing well, thank you.", true, blue);

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

        JButton sendButton = new JButton(new ImageIcon("src/img/send.png")); // send button icon
        sendButton.setBackground(new Color(0x128C7E));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
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
        } catch (BadLocationException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error appending message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom List Cell Renderer
    class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setOpaque(true); // Make sure the label is opaque to show the background color

            // Set background color based on selection state
            if (isSelected) {
                label.setBackground(new Color(0x1BA995)); // Background color for selected items
                label.setForeground(Color.BLACK); // Set text color for selected items
            } else {
                label.setBackground(new Color(0x2C2D32)); // Background color for non-selected items
                label.setForeground(Color.WHITE); // Set text color for non-selected items
            }

            // Set a visible border
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), // Outer border
                BorderFactory.createEmptyBorder(20, 30, 20, 5) // Inner padding
            ));
            label.setHorizontalAlignment(SwingConstants.LEFT);

            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow();
            chatWindow.setVisible(true);
        });
    }
}