import java.awt.Color;
import javax.swing.JFrame;

public class loginWindow extends JFrame {
    public loginWindow() {
        this.setTitle("Dino Chat Login"); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 500);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0X1C1D22));System.out.println("Login Window");
    }

}
