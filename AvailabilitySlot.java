package org.example;

import java.sql.Date;
import java.sql.Timestamp;

public class AvailabilitySlot {
    private Date startTime;
    private Date endTime;

    // Constructors, getters, and setters
    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setStartTime(Timestamp startTime) {
    }

    public void setEndTime(Timestamp endTime) {
    }
    Doctor doctor = new Doctor();
    String doctorName = doctor.getName();
}