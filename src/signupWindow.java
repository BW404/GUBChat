import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class signupWindow extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton signupButton;
    private final JButton loginButton;

    public signupWindow() {
        this.setTitle("GUB Chat Signup");
        this.setIconImage(new ImageIcon("src/img/gub_logo.png").getImage()); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.setLayout(null);
        this.setResizable(false);
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(0x1C1D22));
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0x128C7E)));

        // gub logo
        JLabel gubLabel = new JLabel(new ImageIcon("src/img/gub_logo.png"));
        gubLabel.setBounds(100, 15, 200, 100);
        this.add(gubLabel);

        // Gub Chat signup label
        JLabel signupLabel = new JLabel("GUB Chat SignUp");
        signupLabel.setBounds(120, 110, 200, 50);
        signupLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        signupLabel.setForeground(Color.WHITE);
        this.add(signupLabel);

        // Username Label
        JLabel usernamLabel = new JLabel("Username:");
        usernamLabel.setBounds(150, 150, 300, 50);
        usernamLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernamLabel.setForeground(Color.WHITE);
        this.add(usernamLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 200, 200, 35);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x128C7E), 1),
            new EmptyBorder(5, 10, 5, 10)));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBackground(new Color(0x2C2D32));
        usernameField.setCaretColor(Color.WHITE);
        this.add(usernameField);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(150, 225, 300, 50);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.WHITE);
        this.add(passwordLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 275, 200, 35);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x128C7E), 1),
            new EmptyBorder(5, 10, 5, 10)));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(0x2C2D32));
        passwordField.setCaretColor(Color.WHITE);
        this.add(passwordField);

        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setBounds(100, 330, 200, 40);
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(new Color(0x128C7E));
        signupButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        signupButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        signupButton.setFocusPainted(false);
        this.add(signupButton);

        // "Already have an account? Login" Button
        loginButton = new JButton("Already have an account? Login");
        loginButton.setBounds(100, 400, 200, 30);
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setForeground(new Color(0x128C7E));
        loginButton.setBackground(new Color(0x1C1D22));
        loginButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        this.add(loginButton);

        // Add ActionListener to the signup button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Add username and password in the database 
                // TODO: Check if the username already exists
                // TODO: If the username already exists, show a message dialog "Username already exists"
                // TODO: If the username doesn't exist, add the user to the database and show a message dialog "Signup successful"
                // TODO: If the username and password are valid, close the signup window and open the login window
                // TODO: If the username and password are invalid, show a message dialog "Invalid username or password"
            }
        });

        // Add ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new loginWindow();
            }
        });

        this.setVisible(true);
    }
}
