import java.sql.*;
import java.util.Scanner;

public class deposittransaction {

    // Database connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String JDBC_USERNAME = "your_username";
    private static final String JDBC_PASSWORD = "your_password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Collect account number and deposit amount from the user
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter the amount to deposit: ₹");
        double depositAmount = scanner.nextDouble();
        
        // Call the deposit function
        depositMoney(accountNumber, depositAmount);

        scanner.close();
    }

    // Function to deposit money
    public static void depositMoney(String accountNumber, double depositAmount) {
        // SQL queries for updating balance and inserting transaction record
        String updateBalanceQuery = "UPDATE customer SET acc_balance = acc_balance + ? WHERE acc_number = ?";
        String insertTransactionQuery = "INSERT INTO Transaction(account_number, transaction_type, amount, balance_after_transaction, transaction_date, transaction_status) VALUES (?, 'deposit', ?, ?, NOW(),?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Step 1: Update customer balance
            updateBalanceStmt.setDouble(1, depositAmount);  // Set deposit amount
            updateBalanceStmt.setString(2, accountNumber);  // Set account number
            int rowsAffected = updateBalanceStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Step 2: Get the updated balance
                String getBalanceQuery = "SELECT acc_balance FROM customer WHERE acc_number = ?";
                try (PreparedStatement getBalanceStmt = connection.prepareStatement(getBalanceQuery)) {
                    getBalanceStmt.setString(1, accountNumber);  // Set account number to fetch the balance
                    ResultSet resultSet = getBalanceStmt.executeQuery();

                    if (resultSet.next()) {
                        double updatedBalance = resultSet.getDouble("acc_balance");

                        // Step 3: Insert transaction record into the transactions table
                        insertTransactionStmt.setString(1, accountNumber);  // Set account number
                        insertTransactionStmt.setDouble(2, depositAmount);  // Set deposit amount
                        insertTransactionStmt.setDouble(3, updatedBalance);  // Set the updated balance
                        insertTransactionStmt.setString(4, "successful");  // Set transaction status

                        insertTransactionStmt.executeUpdate();
                        System.out.println("Deposit successful! New balance: ₹" + updatedBalance);
                    } else {
                        System.out.println("Account not found.");
                    }
                }
            } else {
                System.out.println("Account not found or invalid details.");
            }

        } catch (SQLException e) {
            System.out.println("Error during deposit.");
            e.printStackTrace();
        }
    }
}
