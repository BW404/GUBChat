import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginWindow extends JFrame { // Changed class name to LoginWindow
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signupButton;

    public LoginWindow() { // Updated constructor name to match class name
        this.setTitle("GUB Chat Login");
        this.setIconImage(new ImageIcon("src/img/gub_logo.png").getImage()); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.getContentPane().setBackground(new Color(0X26272D));
        this.setResizable(false);
        System.out.println("Login Window");
        this.setLayout(null);
        setLocationRelativeTo(null);

        // GUB logo
        JLabel gubLabel = new JLabel(new ImageIcon("src/img/gub_logo.png"));
        gubLabel.setBounds(100, 15, 200, 100);
        this.add(gubLabel);

        // Gub Chat login label
        JLabel loginLabel = new JLabel("GUB Chat Login");
        loginLabel.setBounds(125, 110, 200, 50);
        loginLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        loginLabel.setForeground(Color.WHITE);
        this.add(loginLabel);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(150, 150, 300, 50);
        usernameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        usernameLabel.setForeground(Color.WHITE);
        this.add(usernameLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 200, 200, 30);
        usernameField.setFont(new Font("Dubai Bold", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                usernameField.getBorder(), new EmptyBorder(5, 10, 5, 10)));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBackground(new Color(0X1C1D22));
        usernameField.setCaretColor(Color.WHITE);
        this.add(usernameField);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(150, 225, 300, 50);
        passwordLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        passwordLabel.setForeground(Color.WHITE);
        this.add(passwordLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 275, 200, 30);
        passwordField.setFont(new Font("Dubai Bold", Font.PLAIN, 20));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(), new EmptyBorder(5, 10, 5, 10)));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(0X1C1D22));
        passwordField.setCaretColor(Color.WHITE);
        this.add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(100, 330, 200, 30);
        loginButton.setFont(new Font("Comic Sans MS Bold", Font.PLAIN, 16));
        loginButton.setForeground(Color.BLACK);
        loginButton.setBackground(new Color(0X40a366));
        loginButton.setFocusPainted(false);
        this.add(loginButton);

        // "Don't have an account? Signup" Button
        signupButton = new JButton("Don't have an account? SignUp");
        signupButton.setBounds(100, 400, 200, 30);
        signupButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        signupButton.setBorder(BorderFactory.createEmptyBorder());
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(new Color(0X26272D));
        signupButton.setFocusPainted(false);
        this.add(signupButton);

        // Add ActionListener to the signup button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new signupWindow(); // Updated to match the correct class name
            }
        });

        // Add ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = usernameField.getText();
                String enteredPassword = new String(passwordField.getPassword());
                if (authenticate(enteredUsername, enteredPassword)) {
                    dispose();
                    // Open the chat window directly
                    ChatWindow chatWindow = new ChatWindow(enteredUsername);
                    chatWindow.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        this.setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        // Replace this with your actual authentication logic
        return username.equals("taj") && password.equals("pass") || 
               username.equals("admin") && password.equals("admin") || 
               username.equals("manik") && password.equals("manik");
    }
}
