package com.clinic.appointment.dto;

// Plain request-binding class (JSON -> Java). Kept minimal on purpose -
// no separate response DTOs; responses are built as simple maps in the controller.
public class AppointmentRequest {

    private String patientId;
    private String doctorId;
    private String date;     // bound as String, parsed to LocalDate in the service so we control the error message
    private String timeSlot;
    private String reason;   // optional

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
