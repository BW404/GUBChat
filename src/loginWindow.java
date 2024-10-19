import java.awt.Color;
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
        this.setTitle("Dino Chat Login"); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0X1C1D22));System.out.println("Login Window");

        // this.add(new JLabel("Username:"));
        // this.usernameField = new JTextField();
        // this.add(this.usernameField);

        JLabel usernamLabel = new JLabel("Username:");
        usernamLabel.setForeground(Color.WHITE);
        usernamLabel.setBounds(50, 100, 300, 50);
        this.add(usernamLabel);















    }

}
