import java.sql.*;
import java.util.Scanner;

public class withdrawmoney {

    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String JDBC_USERNAME = "your_username";
    private static final String JDBC_PASSWORD = "your_password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter the amount to withdraw: ₹");
        double withdrawAmount = scanner.nextDouble();

        
        withdrawMoney(accountNumber, withdrawAmount);

        scanner.close();
    }

    
    public static void withdrawMoney(String accountNumber, double withdrawAmount) {
        
        String checkBalanceQuery = "SELECT acc_balance FROM customer WHERE acc_number = ?";
        String updateBalanceQuery = "UPDATE customer SET acc_balance = acc_balance - ? WHERE acc_number = ?";
        String insertTransactionQuery = "INSERT INTO Transaction (account_number, transaction_type, amount, balance_after_transaction, transaction_date,transaction_status) VALUES (?, 'withdrawal', ?, ?, NOW(), ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
             PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

           
            checkBalanceStmt.setString(1, accountNumber);  // Set account number for balance check
            ResultSet resultSet = checkBalanceStmt.executeQuery();

            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("acc_balance");

                
                if (currentBalance >= withdrawAmount) {
                   
                    updateBalanceStmt.setDouble(1, withdrawAmount);  
                    updateBalanceStmt.setString(2, accountNumber);   
                    int rowsAffected = updateBalanceStmt.executeUpdate();

                    if (rowsAffected > 0) {
                     
                        double updatedBalance = currentBalance - withdrawAmount;

                       
                        insertTransactionStmt.setString(1, accountNumber);  
                        insertTransactionStmt.setDouble(2, withdrawAmount);  
                        insertTransactionStmt.setDouble(3, updatedBalance); 
                        insertTransactionStmt.setString(4, "successful");  

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

