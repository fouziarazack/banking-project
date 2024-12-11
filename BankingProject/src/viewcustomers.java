import java.sql.*;
import java.util.Scanner;

public class viewcustomers {
    
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankdb_schema"; // Replace with your database name
    private static final String JDBC_USERNAME = "root"; // Replace with your MySQL username
    private static final String JDBC_PASSWORD = "Fouzu@2005"; // Replace with your MySQL password

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collecting customer name from the user
        System.out.print("Enter customer name to view details: ");
        String customerName = scanner.nextLine().trim();  // Trimming whitespace

        // Fetch and display customer details
        viewBankingDetails(customerName);

        scanner.close();
    }

    public static void viewBankingDetails(String customerName) {
        // SQL query to select customer details based on the name
        String query = "SELECT * FROM customer WHERE name = ?";

        // Establishing database connection and executing the query
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the customer name as the parameter for the query
            preparedStatement.setString(1, customerName);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a record was found
            if (resultSet.next()) {
                // Retrieve customer details from the result set
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                String accountNumber = resultSet.getString("acc_number");
                double balance = resultSet.getDouble("acc_balance");

                // Display customer banking details
                System.out.println("\nBanking Details for " + name + ":");
                System.out.println("Email: " + email);
                System.out.println("Phone: " + phone);
                System.out.println("Address: " + address);
                System.out.println("Account Number: " + accountNumber);
                System.out.println("Account Balance: ₹" + balance);
            } else {
                System.out.println("No customer found with the name: " + customerName);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while retrieving customer details.");
            e.printStackTrace();
        }
    }
}
