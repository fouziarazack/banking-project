import java.sql.*;
import java.util.Scanner;

public class withdrawmoney {

    // Database connection details (replace these with your actual credentials)
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String JDBC_USERNAME = "your_username";
    private static final String JDBC_PASSWORD = "your_password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collect account number and withdrawal amount from the user
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter the amount to withdraw: ₹");
        double withdrawAmount = scanner.nextDouble();

        // Call the withdrawMoney method
        withdrawMoney(accountNumber, withdrawAmount);

        scanner.close();
    }

    // Method to withdraw money
    public static void withdrawMoney(String accountNumber, double withdrawAmount) {
        // SQL queries to check balance, update balance, and log the transaction
        String checkBalanceQuery = "SELECT acc_balance FROM customer WHERE acc_number = ?";
        String updateBalanceQuery = "UPDATE customer SET acc_balance = acc_balance - ? WHERE acc_number = ?";
        String insertTransactionQuery = "INSERT INTO Transaction (account_number, transaction_type, amount, balance_after_transaction, transaction_date,transaction_status) VALUES (?, 'withdrawal', ?, ?, NOW(), ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
             PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Step 1: Check if the account exists and if there is sufficient balance
            checkBalanceStmt.setString(1, accountNumber);  // Set account number for balance check
            ResultSet resultSet = checkBalanceStmt.executeQuery();

            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("acc_balance");

                // Step 2: Check if there are sufficient funds for withdrawal
                if (currentBalance >= withdrawAmount) {
                    // Step 3: Update customer balance
                    updateBalanceStmt.setDouble(1, withdrawAmount);  // Set withdrawal amount
                    updateBalanceStmt.setString(2, accountNumber);   // Set account number
                    int rowsAffected = updateBalanceStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Step 4: Get the updated balance
                        double updatedBalance = currentBalance - withdrawAmount;

                        // Step 5: Insert transaction record into the transactions table
                        insertTransactionStmt.setString(1, accountNumber);  // Set account number
                        insertTransactionStmt.setDouble(2, withdrawAmount);  // Set withdrawal amount
                        insertTransactionStmt.setDouble(3, updatedBalance);  // Set updated balance
                        insertTransactionStmt.setString(4, "successful");  // Set transaction status

                        insertTransactionStmt.executeUpdate();
                        System.out.println("Withdraw successful! New balance: ₹" + updatedBalance);
                    } else {
                        System.out.println("Error in updating balance.");
                    }
                } else {
                    System.out.println("Insufficient funds for this withdrawal.");
                }
            } else {
                System.out.println("Account not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error during withdrawal.");
            e.printStackTrace();
        }
    }
}

