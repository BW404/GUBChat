import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class loginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButnButton;
    public loginWindow() {  
        this.setTitle("GUB Chat Login"); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0X1C1D22));System.out.println("Login Window");
        this.setLayout(null);
        setLocationRelativeTo(null);

        // gub logo
        JLabel gubLabel = new JLabel(new ImageIcon("src\\img\\gub_logo.png"));
        gubLabel.setBounds(100, 20, 200, 200);
        this.add(gubLabel);

        // Username Label
        JLabel usernamLabel = new JLabel("Username:");
        usernamLabel.setBounds(150, 150, 300, 50);
        usernamLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        usernamLabel.setForeground(Color.WHITE);
        this.add(usernamLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 200, 200, 30);
        usernameField.setFont(new Font("Dubai", Font.PLAIN, 14));
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
        passwordField.setFont(new Font("Dubai", Font.PLAIN, 14));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(0X1C1D22));
        passwordField.setCaretColor(Color.WHITE);
        this.add(passwordField);





        

        // Revalidate and repaint to update the layout
        this.revalidate();
        this.repaint();
    
        // Make the frame visible after adding components
        this.setVisible(true);

    }



}
