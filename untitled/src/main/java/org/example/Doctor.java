package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class Doctor {
    private String email;
    private String name;
    private String specialty;
    private String contactDetails;
    private String bio;
    private Set<AvailabilitySlot> availability;

    // Constructors, getters, and setters
    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public String getBio() {
        return bio;
    }

    public Set<AvailabilitySlot> getAvailability() {
        return availability;
    }

    public String getEmail(String email) {
        this.email = email;
        return email;
    }
    public String setEmail(String email) {
        this.email = email;
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvailability(Set<AvailabilitySlot> availability) {
        this.availability = availability;
    }

    public void updateAvailability(Set<AvailabilitySlot> newAvailability, Connection connection) {
        // Update the doctor's availability in the database
        availability = newAvailability;
        updateAvailabilityInDatabase(connection);

        // Notify subscribers about the availability update
        notifySubscribers();
    }

    private void updateAvailabilityInDatabase(Connection connection) {
        // Delete existing availability records
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM availability WHERE doctor_email = ?")) {
            preparedStatement.setString(1, getEmail(email));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert new availability records
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO availability (doctor_email, start_time, end_time) VALUES (?, ?, ?)")) {
            for (AvailabilitySlot slot : availability) {
                preparedStatement.setString(1, getEmail(email));
                preparedStatement.setTimestamp(2, new Timestamp(slot.getStartTime().getTime()));
                preparedStatement.setTimestamp(3, new Timestamp(slot.getEndTime().getTime()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void notifySubscribers() {
        Set<WebSocketClient> subscribers = DoctorAppointmentSystem.doctorSubscribers.getOrDefault(getEmail(email), new HashSet<>());

        for (WebSocketClient client : subscribers) {
            client.sendMessage("Availability updated!");
        }
    }

    // Other methods for doctor profile update, etc.
}
