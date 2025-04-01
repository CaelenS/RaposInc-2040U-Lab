/**
 * Represents a user with an ID, username, and role.
 */
public class User {
    public int userId;
    public String username;
    public String role;

    /**
     * Constructs a new User instance.
     *
     * @param userId   The unique identifier for the user.
     * @param username The username of the user.
     * @param role     The role assigned to the user.
     */
    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}
