import java.sql.*;
import java.util.Scanner;

public class viewcustomers {
    
   
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database"; 
    private static final String JDBC_USERNAME = "your_username"; 
    private static final String JDBC_PASSWORD = "your_password"; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        
        System.out.print("Enter customer name to view details: ");
        String customerName = scanner.nextLine().trim();  

        
        viewBankingDetails(customerName);

        scanner.close();
    }

    public static void viewBankingDetails(String customerName) {
        
        String query = "SELECT * FROM customer WHERE name = ?";

       
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

          
            preparedStatement.setString(1, customerName);

            
            ResultSet resultSet = preparedStatement.executeQuery();

            
            if (resultSet.next()) {
                
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                String accountNumber = resultSet.getString("acc_number");
                double balance = resultSet.getDouble("acc_balance");

         
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
