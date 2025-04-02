import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

//BlackBoxTesting
public class DatabaseConnection {
    private static Dotenv dotenv;
    private static String URL;

    static {
        if (shouldSkipEnv()) {
            System.out.println("[TEST MODE] Skipping .env loading.");
            URL = "";
        } else {
            dotenv = Dotenv.configure().load();
            URL = dotenv.get("DB_URL");
        }
    }

    public static Connection connect() {
        try {
            System.out.println("Attempting to connect to the database...");
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    private static boolean shouldSkipEnv() {
        return Boolean.getBoolean("env.skip");
    }
}
