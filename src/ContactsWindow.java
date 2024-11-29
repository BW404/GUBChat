import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ContactsWindow extends JFrame implements ChatClient.MessageListener {
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private ChatClient chatClient;
    private String username;
    private JLabel connectionStatus;

    public ContactsWindow(String username, ChatClient chatClient) {
        this.username = username;
        this.chatClient = chatClient;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GUB Chat - " + username);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0x1C1D22));
        getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0x128C7E)));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0x26272D));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0x128C7E)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // User Info Panel
        JPanel userInfoPanel = new JPanel(new BorderLayout(10, 0));
        userInfoPanel.setBackground(new Color(0x26272D));
        
        // Username Label
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userInfoPanel.add(usernameLabel, BorderLayout.CENTER);

        // Connection Status
        connectionStatus = new JLabel("Connected");
        connectionStatus.setForeground(new Color(0x4CAF50));
        connectionStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        connectionStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        userInfoPanel.add(connectionStatus, BorderLayout.EAST);

        headerPanel.add(userInfoPanel, BorderLayout.CENTER);

        // Active Chats Label
        JLabel chatsLabel = new JLabel("Active Users");
        chatsLabel.setForeground(new Color(0x128C7E));
        chatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chatsLabel.setBorder(new EmptyBorder(15, 20, 5, 20));
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0x1C1D22));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(chatsLabel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);

        // Contacts List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setBackground(new Color(0x1C1D22));
        contactList.setForeground(Color.WHITE);
        contactList.setSelectionBackground(new Color(0x128C7E));
        contactList.setSelectionForeground(Color.WHITE);
        contactList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactList.setCellRenderer(new ContactListCellRenderer());
        contactList.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Add selection listener to handle chat window creation
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = contactList.getSelectedValue();
                if (selectedUser != null) {
                    ChatManager.getInstance().openChatWindow(selectedUser);
                    contactList.clearSelection(); // Clear selection after opening chat
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(0x1C1D22));
        scrollPane.getVerticalScrollBar().setBackground(new Color(0x2C2D32));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        add(scrollPane, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(0x26272D));
        statusPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0x128C7E)));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 40));
        add(statusPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onMessageReceived(String message) {
        if (message.startsWith("ACTIVE_CLIENTS:")) {
            String[] clients = message.substring("ACTIVE_CLIENTS:".length()).split(",");
            updateContactList(clients);
        }
    }

    @Override
    public void onFileReceived(FileWrapper file) {
        // Files are handled in individual chat windows
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

    // Custom cell renderer for contacts list
    private class ContactListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, 
                    index, isSelected, cellHasFocus);
            
            // Create a round status indicator
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBackground(isSelected ? new Color(0x128C7E) : new Color(0x1C1D22));
            
            // Status dot
            JPanel statusDot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0x4CAF50));
                    g2d.fillOval(0, 0, 8, 8);
                    g2d.dispose();
                }
            };
            statusDot.setOpaque(false);
            statusDot.setPreferredSize(new Dimension(8, 8));
            
            // User label
            JLabel userLabel = new JLabel(value.toString());
            userLabel.setForeground(label.getForeground());
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            panel.add(statusDot, BorderLayout.WEST);
            panel.add(userLabel, BorderLayout.CENTER);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C2D32)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
            ));
            
            return panel;
        }
    }
}
