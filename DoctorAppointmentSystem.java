package org.example;
import java.sql.*;
import java.util.*;

import static org.example.Utils.loginUser;

public class DoctorAppointmentSystem {
    private static Map<String, Doctor> doctors = new HashMap<>();
    static Map<String, Set<WebSocketClient>> doctorSubscribers = new HashMap<>();

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctorappointment01", "root", "Demo@123")) {
            createTablesIfNotExists(connection);

            Scanner scanner = new Scanner(System.in);
            Admin admin = new Admin(connection, "AdminPM", "Test@1234");

//            while (true) {
                System.out.println("1. Doctor Login\n2. User Login\n3. Admin Login\n4. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        doctorLogin(connection);
                        break;
                    case 2:
                        loginUser(connection);
                        break;
                    case 3:
                        adminLogin(admin);
                        break;
                    case 4:
                        System.out.println("Exiting program.");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExists(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createDoctorTableQuery = "CREATE TABLE IF NOT EXISTS doctors (" +
                    "email VARCHAR(255) PRIMARY KEY," +
                    "name VARCHAR(255)," +
                    "specialty VARCHAR(255)," +
                    "contact_details VARCHAR(255)," +
                    "bio TEXT)";
            statement.execute(createDoctorTableQuery);

            String createAvailabilityTableQuery = "CREATE TABLE IF NOT EXISTS availability (" +
                    "doctor_email VARCHAR(255)," +
                    "start_time TIMESTAMP," +
                    "end_time TIMESTAMP," +
                    "FOREIGN KEY (doctor_email) REFERENCES doctors(email) ON DELETE CASCADE)";
            statement.execute(createAvailabilityTableQuery);

            String createAppointmentTableQuery = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "start_time TIMESTAMP," +
                    "end_time TIMESTAMP," +
                    "doctor_email VARCHAR(255)," +
                    "patient_email VARCHAR(255)," +
                    "FOREIGN KEY (doctor_email) REFERENCES doctors(email) ON DELETE CASCADE)";
            statement.execute(createAppointmentTableQuery);
        }
    }

    private static void doctorLogin(Connection connection) {
        System.out.print("Enter doctor's email: ");
        Scanner scanner = new Scanner(System.in);
        String email = scanner.nextLine();

        if (!isDoctorExists(connection, email)) {
            System.out.println("Doctor not found. Please register first.");
            return;
        }

        Doctor doctor = getDoctorFromDatabase(connection, email);
        System.out.println("Welcome, Dr. " + doctor.setEmail(email));

        // Perform doctor-specific actions here (profile update, availability, etc.)
        // You can add a menu for doctor actions.

        // Simulate real-time updates using WebSocket
        doctorSubscribers.put(email, new HashSet<>());
        WebSocketClient client = new WebSocketClient(doctor);
        doctorSubscribers.get(email).add(client);
        client.start();
    }

    private static boolean isDoctorExists(Connection connection, String email) {
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

    private static Doctor getDoctorFromDatabase(Connection connection, String email) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM doctors WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setEmail(resultSet.getString("email"));
                    doctor.setName(resultSet.getString("name"));
                    doctor.setSpecialty(resultSet.getString("specialty"));
                    doctor.setContactDetails(resultSet.getString("contact_details"));
                    doctor.setBio(resultSet.getString("bio"));
                    doctor.setAvailability(getAvailabilityFromDatabase(connection, email));
                    return doctor;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Set<AvailabilitySlot> getAvailabilityFromDatabase(Connection connection, String doctorEmail) {
        Set<AvailabilitySlot> availabilitySlots = new HashSet<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM availability WHERE doctor_email = ?")) {
            preparedStatement.setString(1, doctorEmail);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    AvailabilitySlot slot = new AvailabilitySlot();
                    slot.setStartTime(resultSet.getTimestamp("start_time"));
                    slot.setEndTime(resultSet.getTimestamp("end_time"));
                    availabilitySlots.add(slot);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availabilitySlots;
    }

    // Other methods for doctor profile update, availability, appointment scheduling


    private static boolean isEmailExists(Connection connection, String email, String tableName) {
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

    private static boolean isPasswordCorrect(Connection connection, String email, String password, String tableName) {
        String query = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
    private static void adminLogin(Admin admin) {
        System.out.print("Enter admin username: ");
        Scanner scanner = new Scanner(System.in);
        String enteredUsername = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String enteredPassword = scanner.nextLine();

        if (admin.login() && enteredUsername.equals(admin.getUsername()) && enteredPassword.equals(admin.getPassword())) {
            adminDashboard(admin);
        } else {
            System.out.println("Invalid username or password for admin.");
        }
    }
    private static void adminDashboard(Admin admin) {
        System.out.println("Admin Dashboard");
        // Display admin options and menu
        admin.manageSystem();
    }

}