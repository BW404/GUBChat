import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.geom.RoundRectangle2D;

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

class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setBackground(new Color(0x40a366));
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isRollover()) {
            g.setColor(new Color(0x32a052));
        } else {
            g.setColor(getBackground());
        }
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
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
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0x1C1D22));

        // Left Section: Contact List
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0x26272D));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Search Bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(0x26272D));
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField("Search");
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBackground(new Color(0xF0F0F0));
        searchPanel.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactListModel.addElement("John Doe");
        contactListModel.addElement("Jane Smith");
        contactListModel.addElement("Alice Johnson");
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0xD0D0D0));
        contactList.setSelectionForeground(Color.BLACK);
        contactList.setFont(new Font("Roboto", Font.PLAIN, 14));
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
        JLabel chatTitle = new JLabel("Chat with John Doe");
        chatTitle.setForeground(Color.WHITE);
        chatTitle.setFont(new Font("Arial", Font.BOLD, 20));
        chatHeader.add(chatTitle, BorderLayout.WEST);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setContentType("text/html");
        messageArea.setBackground(new Color(0x1C1D22));
        messageArea.setForeground(Color.WHITE);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(0x1C1D22));
        inputPanel.setLayout(new BorderLayout());

        writeMessageField = new JTextField();
        writeMessageField.setBackground(new Color(0xF0F0F0));
        writeMessageField.setForeground(Color.BLACK);
        writeMessageField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(writeMessageField, BorderLayout.CENTER);

        RoundedButton sendButton = new RoundedButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = writeMessageField.getText();
                if (!message.isEmpty()) {
                    appendMessage("You: " + message, new Color(0x40a366));
                    writeMessageField.setText("");
                }
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void appendMessage(String message, Color color) {
        String htmlMessage = "<div style='color:" + toHex(color) + ";'>" + message + "</div>";
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) messageArea.getDocument();
        messageArea.setEditorKit(kit);
        try {
            doc.insertString(doc.getLength(), htmlMessage, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow();
            chatWindow.setVisible(true);
        });
    }
}