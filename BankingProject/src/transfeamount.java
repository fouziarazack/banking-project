import java.sql.*;
import java.util.Scanner;

public class transferamount {

    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String JDBC_USERNAME = "your_username";
    private static final String JDBC_PASSWORD = "your_password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        
        System.out.print("Enter your source account number: ");
        String sourceAccount = scanner.nextLine();

        System.out.print("Enter the destination account number: ");
        String destinationAccount = scanner.nextLine();

        System.out.print("Enter the amount to transfer: ₹");
        double transferAmount = scanner.nextDouble();

       
        transferMoney(sourceAccount, destinationAccount, transferAmount);

        scanner.close();
    }

    
    public static void transferMoney(String sourceAccount, String destinationAccount, double transferAmount) {
      
        String checkBalanceQuery = "SELECT acc_balance FROM customer WHERE acc_number = ?";
        String updateSourceBalanceQuery = "UPDATE customer SET acc_balance = acc_balance - ? WHERE acc_number = ?";
        String updateDestinationBalanceQuery = "UPDATE customer SET acc_balance = acc_balance + ? WHERE acc_number = ?";
        String insertTransactionQuery = "INSERT INTO Transaction (account_number, transaction_type, amount, balance_after_transaction, transaction_date,  transaction_status) VALUES (?, 'transfer', ?, ?, NOW(),  ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
             PreparedStatement updateSourceBalanceStmt = connection.prepareStatement(updateSourceBalanceQuery);
             PreparedStatement updateDestinationBalanceStmt = connection.prepareStatement(updateDestinationBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            
            checkBalanceStmt.setString(1, sourceAccount);
            ResultSet resultSet = checkBalanceStmt.executeQuery();

            if (resultSet.next()) {
                double sourceBalance = resultSet.getDouble("acc_balance");

                
                if (sourceBalance >= transferAmount) {

                    
                    checkBalanceStmt.setString(1, destinationAccount);
                    resultSet = checkBalanceStmt.executeQuery();

                    if (resultSet.next()) {
                        
                        updateSourceBalanceStmt.setDouble(1, transferAmount);
                        updateSourceBalanceStmt.setString(2, sourceAccount);
                        int rowsAffectedSource = updateSourceBalanceStmt.executeUpdate();

                        
                        updateDestinationBalanceStmt.setDouble(1, transferAmount);
                        updateDestinationBalanceStmt.setString(2, destinationAccount);
                        int rowsAffectedDestination = updateDestinationBalanceStmt.executeUpdate();

                        
                        if (rowsAffectedSource > 0 && rowsAffectedDestination > 0) {
                          
                            double updatedSourceBalance = sourceBalance - transferAmount;

                           
                            checkBalanceStmt.setString(1, destinationAccount);
                            resultSet = checkBalanceStmt.executeQuery();
                            resultSet.next();
                            double updatedDestinationBalance = resultSet.getDouble("acc_balance");

                           
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
