import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Provides a utility method to establish a connection to a database using
 * environment variables for configuration.
 */

/**
 * Default constructor for DatabaseConnection
 */
public class DatabaseConnection {
    
    /**
     * Loads environment variables from a .env file.
     */
    private static final Dotenv dotenv = Dotenv.load();
    
    /**
     * The database URL retrieved from environment variables.
     */
    private static final String URL = dotenv.get("DB_URL");

    /**
     * Establishes a connection to the database.
     *
     * @return a {@link Connection} object if the connection is successful, otherwise {@code null}.
     */
    public static Connection connect() {
        try {
            System.out.println("Attempting to connect to the database...");
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
}
