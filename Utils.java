package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Utils {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, Integer> loginAttempts = new HashMap<>();
    public static void loginUser(Connection connection) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        if (!isEmailExists(connection, email)) {
            System.out.println("Email not found. Please register first.");
            return;
        }

        int attempts = loginAttempts.getOrDefault(email, 0);

        if (attempts >= 3) {
            System.out.println("Too many incorrect login attempts. Exiting.");
            System.exit(0);
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (isPasswordCorrect(connection, email, password)) {
            System.out.println("Login successful!");
            loginAttempts.remove(email); // Reset login attempts on successful login
        } else {
            System.out.println("Incorrect password. Try again.");
            loginAttempts.put(email, attempts + 1);
        }
        return;
    }
    public static boolean isEmailExists(Connection connection, String email) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isPasswordCorrect(Connection connection, String email, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
