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

/**
 * A graphical user interface for user login.
 * This class provides a login window with fields for username and password,
 * and validates credentials using a database connection.
 */
public class LoginGUI {
    
    /** The main login window frame. */
    private JFrame loginFrame;
    /** The input field for the username. */
    private JTextField usernameField;
    /** The input field for the password (masked). */
    private JPasswordField passwordField;

    /**
     * Constructs the Login GUI and initializes database connection.
     * If the database connection fails, an error message is displayed.
     */
    public LoginGUI() {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserAuth auth = new UserAuth(conn);

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
    /**
     * Constructs the Login GUI for test mode (skips database connection).
     * @param testMode If true, skips database connection and creates a fake login.
     */
    public LoginGUI(boolean testMode) {
        if (!testMode) {
            // Normal mode: connect to real database
            Connection conn = DatabaseConnection.connect();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            UserAuth auth = new UserAuth(conn);
            setupLoginFrame(auth); // Build the GUI with real authentication
        } else {
            // Test mode: skip database connection
            setupLoginFrame(null); // Build the GUI without authentication
        }
    }


    /**
     * Sets up the login frame window.
     * @param auth The UserAuth object for login validation (can be null in test mode).
     */
    private void setupLoginFrame(UserAuth auth) {
        loginFrame = new JFrame("Login");
        loginFrame.setSize(800, 600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        loginFrame.add(panel);
        placeComponents(panel, auth);  // Setup the UI components with login logic

        loginFrame.setVisible(true);
    }


    /**
     * Places UI components on the panel and sets up the login button functionality.
     *
     * @param panel The panel where components are added.
     * @param auth  The {@link UserAuth} instance used for login authentication.
     */
    private void placeComponents(JPanel panel, UserAuth auth) {
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
            if (auth == null) {
                // Fakes a successful login, in Test Mode
                loginFrame.dispose();
                showLoadingScreen(new User(1, "TestUser", "admin"));
            } else {
                // Real Database authentication
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User user = auth.login(username, password);
                if (user != null) {
                    loginFrame.dispose();
                    showLoadingScreen(user);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.");
                }
            }
        });
    }

    /**
     * Displays a loading screen before transitioning to the product catalog.
     *
     * @param user The authenticated user instance.
     */
    private void showLoadingScreen(User user) {
        JFrame loadingFrame = new JFrame("Loading");
        loadingFrame.setSize(800, 600);
        loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadingFrame.setLocationRelativeTo(null);
        
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
    }

    /**
     * The main entry point for launching the login GUI.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new LoginGUI();
    }
}
