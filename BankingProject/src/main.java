import java.sql.Connection;

public class main {
    public static void main(String[] args) {
        Connection connection = database.getConnection();
        if (connection != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}
