import java.sql.*;
import java.util.Scanner;

public class deposittransaction {

    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String JDBC_USERNAME = "your_username";
    private static final String JDBC_PASSWORD = "your_password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter the amount to deposit: ₹");
        double depositAmount = scanner.nextDouble();
        
        
        depositMoney(accountNumber, depositAmount);

        scanner.close();
    }

   
    public static void depositMoney(String accountNumber, double depositAmount) {
        
        String updateBalanceQuery = "UPDATE customer SET acc_balance = acc_balance + ? WHERE acc_number = ?";
        String insertTransactionQuery = "INSERT INTO Transaction(account_number, transaction_type, amount, balance_after_transaction, transaction_date, transaction_status) VALUES (?, 'deposit', ?, ?, NOW(),?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            
            updateBalanceStmt.setDouble(1, depositAmount);  // Set deposit amount
            updateBalanceStmt.setString(2, accountNumber);  // Set account number
            int rowsAffected = updateBalanceStmt.executeUpdate();

            if (rowsAffected > 0) {
                
                String getBalanceQuery = "SELECT acc_balance FROM customer WHERE acc_number = ?";
                try (PreparedStatement getBalanceStmt = connection.prepareStatement(getBalanceQuery)) {
                    getBalanceStmt.setString(1, accountNumber);  // Set account number to fetch the balance
                    ResultSet resultSet = getBalanceStmt.executeQuery();

                    if (resultSet.next()) {
                        double updatedBalance = resultSet.getDouble("acc_balance");

                       
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
