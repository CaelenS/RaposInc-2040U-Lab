import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Handles user authentication.
 */
public class UserAuth {
    private Connection conn;

    /**
     * Constructs a UserAuth instance with a database connection.
     *
     * @param conn The database connection.
     */
    public UserAuth(Connection conn) {
        this.conn = conn;
    }

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username The user's username.
     * @param password The plaintext password input.
     * @return The authenticated User object if credentials are valid, otherwise null.
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("User_ID"),
                    rs.getString("Username"),
                    rs.getString("Role")
                );
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }
}

