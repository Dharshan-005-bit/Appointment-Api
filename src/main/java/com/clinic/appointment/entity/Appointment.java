package com.clinic.appointment.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "appointments",
    // DB-level safety net backing up the service-layer checks (see AppointmentService)
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_doctor_slot", columnNames = {"doctor_id", "date", "time_slot"}),
        @UniqueConstraint(name = "uq_patient_day", columnNames = {"patient_id", "date"})
    }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // internal numeric PK, used to build the public appointmentId (AP001, AP002...)

    // NOT nullable=false here on purpose: this gets generated from the auto-increment `id`
    // AFTER the first insert (see AppointmentService.bookAppointment), so it's briefly null.
    @Column(name = "appointment_id", unique = true)
    private String appointmentId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(name = "reason")
    private String reason;

    public Appointment() {
    }

    public Appointment(String patientId, String doctorId, LocalDate date, String timeSlot, String reason) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
