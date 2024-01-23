package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
    private Connection connection;
    private String username;
    private String password;

    public Admin(Connection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean login() {
        // Implement login logic
        // Check if the provided username and password are correct
        return username.equals("AdminPM") && password.equals("Test@1234");
    }
    public void manageSystem() {
//        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. Add Doctor");
            System.out.println("2. Remove Doctor");
            System.out.println("3. Remove User");
            System.out.println("4. Filter doctor info");
            System.out.println("5. View All Doctors");
            System.out.println("6. View All Users");
            System.out.println("7. Exit");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    addDoctor();
                    break;
                case 2:
                    removeDoctor();
                    break;
                case 3:
                    removeUser();
                    break;
                case 4:
                    searchDoctors();
                    break;
                case 5:
                    viewAllDoctors();
                    break;
                case 6:
                    viewAllUsers();
                    break;
                case 7:
                    System.out.println("Exiting admin menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
//        }
    }

    public void viewAllDoctors() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM doctors")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("test" + resultSet);
                while (resultSet.next()) {
                    System.out.println("Doctor Email: " + resultSet.getString("email"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Specialty: " + resultSet.getString("specialty"));
                    System.out.println("Contact Details: " + resultSet.getString("contact_details"));
                    System.out.println("Bio: " + resultSet.getString("bio"));
                    System.out.println("---------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("test" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewAllUsers() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("User Email: " + resultSet.getString("email"));
                    // Add more user details as needed
                    System.out.println("---------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void addDoctor() {
        System.out.println("Enter doctor details:");
        Scanner scanner = new Scanner(System.in);

        // Validate Email
        String email;
        do {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
            }
        } while (!isValidEmail(email) || isEmailExists(connection, email, "doctors"));

        // Validate Name
        String name;
        do {
            System.out.print("Name: ");
            name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("Name cannot be empty. Please enter a valid name.");
            }
        } while (name.trim().isEmpty());

        // Validate Specialty
        String specialty;
        do {
            System.out.print("Specialty: ");
            specialty = scanner.nextLine();
            if (specialty.trim().isEmpty()) {
                System.out.println("Specialty cannot be empty. Please enter a valid specialty.");
            }
        } while (specialty.trim().isEmpty());

        // Validate Contact Details
        String contactDetails;
        do {
            System.out.print("Contact Details: ");
            contactDetails = scanner.nextLine();
            if (contactDetails.trim().isEmpty()) {
                System.out.println("Contact Details cannot be empty. Please enter valid contact details.");
            }
        } while (contactDetails.trim().isEmpty());

        // Validate Bio
        String bio;
        do {
            System.out.print("Bio: ");
            bio = scanner.nextLine();
            if (bio.trim().isEmpty()) {
                System.out.println("Bio cannot be empty. Please enter a valid bio.");
            }
        } while (bio.trim().isEmpty());

        // Get doctor details and add to the database
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO doctors (email, name, specialty, contact_details, bio) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, specialty);
            preparedStatement.setString(4, contactDetails);
            preparedStatement.setString(5, bio);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add doctor.");
        }

        System.out.println("Doctor added successfully.");
    }

    private boolean isValidEmail(String email) {
        // Add your email validation logic here
        // Example: You can use regular expressions to validate the email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void removeDoctor() {
        System.out.print("Enter doctor's email to remove: ");
        Scanner scanner = new Scanner(System.in);
        String email = scanner.nextLine();

        if (!isDoctorExists(connection, email)) {
            System.out.println("Doctor not found.");
            return;
        }

        // Remove doctor from the database
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM doctors WHERE email = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to remove doctor.");
        }

        System.out.println("Doctor removed successfully.");
    }

    private void removeUser() {
        System.out.print("Enter user's email to remove: ");
        Scanner scanner = new Scanner(System.in);
        String email = scanner.nextLine();

        if (!isEmailExists(connection, email, "users")) {
            System.out.println("User not found.");
            return;
        }

        // Remove user from the database
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE email = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to remove user.");
        }

        System.out.println("User removed successfully.");
    }

    private void searchDoctors() {
        System.out.println("Search doctors by:\n1. Name\n2. Specialty\n3. Location\n4. Exit");
        Scanner scanner = new Scanner(System.in);
        int searchOption = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (searchOption) {
            case 1:
                searchDoctorsByName();
                break;
            case 2:
                searchDoctorsBySpecialty();
                break;
            case 3:
                // Implement location search
                break;
            case 4:
                System.out.println("Exiting search menu.");
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void searchDoctorsByName() {
        System.out.print("Enter doctor's name to search: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        // Perform search based on the name
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM doctors WHERE name LIKE ?")) {
            preparedStatement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Display search results
                while (resultSet.next()) {
                    System.out.println("Doctor Email: " + resultSet.getString("email"));
                    // Display other doctor details as needed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchDoctorsBySpecialty() {
        System.out.print("Enter doctor's specialty to search: ");
        Scanner scanner = new Scanner(System.in);
        String specialty = scanner.nextLine();

        // Perform search based on the specialty
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM doctors WHERE specialty LIKE ?")) {
            preparedStatement.setString(1, "%" + specialty + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Display search results
                while (resultSet.next()) {
                    System.out.println("Doctor Email: " + resultSet.getString("email"));
                    // Display other doctor details as needed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailExists(Connection connection, String email, String tableName) {
        String query = "SELECT * FROM " + tableName + " WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDoctorExists(Connection connection, String email) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM doctors WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
