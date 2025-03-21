import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class LoginGUI {
    private JFrame loginFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserAuth auth = new UserAuth(conn);

        // Create login frame sized 800x600 and center it
        loginFrame = new JFrame("Login");
        loginFrame.setSize(800, 600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);  // Center on screen

        JPanel panel = new JPanel();
        panel.setLayout(null);
        loginFrame.add(panel);
        placeComponents(panel, auth);

        loginFrame.setVisible(true);
    }

    private void placeComponents(JPanel panel, UserAuth auth) {
        // Adjust components to be positioned to appear centered in the 800x600 panel
        int panelWidth = 800;
        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds((panelWidth - 300) / 2, 100, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds((panelWidth - 300) / 2 + 90, 100, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds((panelWidth - 300) / 2, 140, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds((panelWidth - 300) / 2 + 90, 140, 160, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds((panelWidth - 80) / 2, 200, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = auth.login(username, password);
            if (user != null) {
                loginFrame.dispose();
                // Show a loading screen before transitioning to the catalogue
                JFrame loadingFrame = new JFrame("Loading");
                loadingFrame.setSize(800, 600);
                loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loadingFrame.setLocationRelativeTo(null);  // Center the loading frame
                
                JPanel loadingPanel = new JPanel(new BorderLayout());
                JLabel loadingLabel = new JLabel("Loading, please wait...", SwingConstants.CENTER);
                loadingPanel.add(loadingLabel, BorderLayout.CENTER);
                loadingFrame.add(loadingPanel);
                loadingFrame.setVisible(true);

                Timer timer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        loadingFrame.dispose();
                        new ProductCatalogueGUI(user);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.");
            }
        });
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}
