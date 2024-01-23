package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.example.Utils.isEmailExists;
import static org.example.Utils.loginUser;

public class UserAuthenticationSystem {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, Integer> loginAttempts = new HashMap<>();

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorappointment01", "root", "Demo@123")) {
            createTableIfNotExists(connection);

            while (true) {
                System.out.println("1. Register\n2. Login\n3. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        registerUser(connection);
                        break;
                    case 2:
                        loginUser(connection);
                        break;
                    case 3:
                        System.out.println("Exiting program.");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                    "email VARCHAR(255) PRIMARY KEY," +
                    "password VARCHAR(255))";
            statement.execute(createTableQuery);
        }
    }

    private static void registerUser(Connection connection) {
        System.out.println("User Registration:");
        System.out.print("Enter name (uppercase, min 6, max 25 chars, no numbers/special chars, can have spaces): ");
        String name = scanner.nextLine().toUpperCase();

        System.out.print("Enter email (should have @gmail.com): ");
        String email = scanner.nextLine();

        // Validate email
        if (!email.matches(".+@gmail\\.com")) {
            System.out.println("Invalid email format. Please try again.");
            return;
        }

        // Check for duplicate email
        if (isEmailExists(connection, email)) {
            System.out.println("Email already exists. Please use a different email.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Validate password strength
        if (!isValidPassword(password)) {
            System.out.println("Weak password. Please use a stronger password.");
            return;
        }

        // Register user in the database
        addUserToDatabase(connection, email, password);
        System.out.println("Registration successful!");
    }



    private static void addUserToDatabase(Connection connection, String email, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private static boolean isValidPassword(String password) {
        // Implement your password strength requirements here
        // For simplicity, let's say the password should be at least 8 characters long
        return password.length() >= 8;
    }
}
