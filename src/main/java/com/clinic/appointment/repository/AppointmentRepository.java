package com.clinic.appointment.repository;

import com.clinic.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // --- Conflict checks ---

    // Doctor conflict: same doctor, same date, same time slot already taken
    boolean existsByDoctorIdAndDateAndTimeSlot(String doctorId, LocalDate date, String timeSlot);

    // Patient conflict: patient already has ANY appointment (with any doctor) that day
    boolean existsByPatientIdAndDate(String patientId, LocalDate date);

    // --- Lookups ---

    Optional<Appointment> findByAppointmentId(String appointmentId);

    // Used to compute available slots: all slots already booked for a doctor on a date
    List<Appointment> findByDoctorIdAndDate(String doctorId, LocalDate date);
}
