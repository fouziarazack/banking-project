import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerInsert {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collecting customer details from the user
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();

        System.out.print("Enter customer phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter customer address: ");
        String address = scanner.nextLine();

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Enter initial account balance: ");
        double accountBalance = scanner.nextDouble();

        // SQL query to insert data
        String query = "INSERT INTO customer (name, email, phone, address, acc_number, acc_balance) VALUES (?,?,?,?,?,?)";

        // Insert data into the table
        try (Connection connection = database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the values for placeholder in the query
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, accountNumber);
            preparedStatement.setDouble(6, accountBalance);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Customer inserted successfully!");
            } else {
                System.out.println("Failed to insert customer.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while inserting the customer.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
