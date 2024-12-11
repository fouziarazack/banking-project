import java.sql.*;
import java.util.Scanner;

public class transfeamount {

    // Database connection details (replace with actual credentials)
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankdb_schema";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "Fouzu@2005";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collect account numbers and transfer amount from the user
        System.out.print("Enter your source account number: ");
        String sourceAccount = scanner.nextLine();

        System.out.print("Enter the destination account number: ");
        String destinationAccount = scanner.nextLine();

        System.out.print("Enter the amount to transfer: ₹");
        double transferAmount = scanner.nextDouble();

        // Call the transferMoney method
        transferMoney(sourceAccount, destinationAccount, transferAmount);

        scanner.close();
    }

    // Method to transfer money from one account to another
    public static void transferMoney(String sourceAccount, String destinationAccount, double transferAmount) {
        // SQL queries to check balances, update accounts, and log transactions
        String checkBalanceQuery = "SELECT acc_balance FROM customer WHERE acc_number = ?";
        String updateSourceBalanceQuery = "UPDATE customer SET acc_balance = acc_balance - ? WHERE acc_number = ?";
        String updateDestinationBalanceQuery = "UPDATE customer SET acc_balance = acc_balance + ? WHERE acc_number = ?";
        String insertTransactionQuery = "INSERT INTO Transaction (account_number, transaction_type, amount, balance_after_transaction, transaction_date,  transaction_status) VALUES (?, 'transfer', ?, ?, NOW(),  ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
             PreparedStatement updateSourceBalanceStmt = connection.prepareStatement(updateSourceBalanceQuery);
             PreparedStatement updateDestinationBalanceStmt = connection.prepareStatement(updateDestinationBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Step 1: Check if source account exists and has sufficient funds
            checkBalanceStmt.setString(1, sourceAccount);
            ResultSet resultSet = checkBalanceStmt.executeQuery();

            if (resultSet.next()) {
                double sourceBalance = resultSet.getDouble("acc_balance");

                // Check if source account has sufficient funds
                if (sourceBalance >= transferAmount) {

                    // Step 2: Check if destination account exists
                    checkBalanceStmt.setString(1, destinationAccount);
                    resultSet = checkBalanceStmt.executeQuery();

                    if (resultSet.next()) {
                        // Step 3: Deduct amount from source account
                        updateSourceBalanceStmt.setDouble(1, transferAmount);
                        updateSourceBalanceStmt.setString(2, sourceAccount);
                        int rowsAffectedSource = updateSourceBalanceStmt.executeUpdate();

                        // Step 4: Add amount to destination account
                        updateDestinationBalanceStmt.setDouble(1, transferAmount);
                        updateDestinationBalanceStmt.setString(2, destinationAccount);
                        int rowsAffectedDestination = updateDestinationBalanceStmt.executeUpdate();

                        // Step 5: If both balances updated successfully, insert transaction record
                        if (rowsAffectedSource > 0 && rowsAffectedDestination > 0) {
                            // Get the new balance of the source account
                            double updatedSourceBalance = sourceBalance - transferAmount;

                            // Get the new balance of the destination account
                            checkBalanceStmt.setString(1, destinationAccount);
                            resultSet = checkBalanceStmt.executeQuery();
                            resultSet.next();
                            double updatedDestinationBalance = resultSet.getDouble("acc_balance");

                            // Insert transaction records for both accounts (source and destination)
                            insertTransactionStmt.setString(1, sourceAccount); // Source account
                            insertTransactionStmt.setDouble(2, transferAmount);
                            insertTransactionStmt.setDouble(3, updatedSourceBalance);
                           insertTransactionStmt.setString(4, "successful");
                            insertTransactionStmt.executeUpdate();

                            insertTransactionStmt.setString(1, destinationAccount); // Destination account
                            insertTransactionStmt.setDouble(2, transferAmount);
                            insertTransactionStmt.setDouble(3, updatedDestinationBalance);
                      
                            insertTransactionStmt.setString(4, "successful");
                            insertTransactionStmt.executeUpdate();

                            System.out.println("Transfer successful!");
                            System.out.println("New balance in source account: ₹" + updatedSourceBalance);
                            System.out.println("New balance in destination account: ₹" + updatedDestinationBalance);
                        } else {
                            System.out.println("Error in updating account balances.");
                        }
                    } else {
                        System.out.println("Destination account not found.");
                    }
                } else {
                    System.out.println("Insufficient funds in source account.");
                }
            } else {
                System.out.println("Source account not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error during the transfer.");
            e.printStackTrace();
        }
    }
}
