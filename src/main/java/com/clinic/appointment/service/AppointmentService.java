package com.clinic.appointment.service;

import com.clinic.appointment.dto.AppointmentRequest;
import com.clinic.appointment.entity.Appointment;
import com.clinic.appointment.exception.ConflictException;
import com.clinic.appointment.exception.NotFoundException;
import com.clinic.appointment.exception.ValidationException;
import com.clinic.appointment.repository.AppointmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AppointmentService {

    // Fixed clinic slots for the day
    private static final List<String> ALL_SLOTS = List.of("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");
    private static final Set<String> VALID_SLOTS = Set.copyOf(ALL_SLOTS);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Books an appointment after validating input and checking both conflict types.
     *
     * Conflict checking, explained:
     * 1) DOCTOR conflict -> existsByDoctorIdAndDateAndTimeSlot(doctorId, date, timeSlot)
     *    Same doctor can't be double-booked in the same slot on the same day.
     * 2) PATIENT conflict -> existsByPatientIdAndDate(patientId, date)
     *    A patient can only hold one appointment per day, regardless of doctor or slot,
     *    so this check ignores time_slot entirely.
     *
     * Both checks run inside a single @Transactional method, and the entity also carries
     * DB-level unique constraints (see Appointment.java) as a safety net against race
     * conditions if this service were ever scaled to multiple instances hitting the same DB.
     */
    @Transactional
    public synchronized Appointment bookAppointment(AppointmentRequest request) {
        validateRequest(request);

        LocalDate date = parseDate(request.getDate());
        String timeSlot = request.getTimeSlot();

        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException("date must not be in the past");
        }
        if (!VALID_SLOTS.contains(timeSlot)) {
            throw new ValidationException("time_slot must be one of: " + ALL_SLOTS);
        }

        if (appointmentRepository.existsByDoctorIdAndDateAndTimeSlot(request.getDoctorId(), date, timeSlot)) {
            throw new ConflictException("Doctor " + request.getDoctorId() + " already has an appointment at "
                    + timeSlot + " on " + date);
        }
        if (appointmentRepository.existsByPatientIdAndDate(request.getPatientId(), date)) {
            throw new ConflictException("Patient " + request.getPatientId()
                    + " already has an appointment booked on " + date);
        }

        Appointment appointment = new Appointment(request.getPatientId(), request.getDoctorId(), date, timeSlot,
                request.getReason());

        try {
            Appointment saved = appointmentRepository.save(appointment);
            // Generate sequential ID from the DB-assigned primary key, e.g. id=1 -> "AP001"
            saved.setAppointmentId(String.format("AP%03d", saved.getId()));
            return appointmentRepository.save(saved);
        } catch (DataIntegrityViolationException e) {
            // Belt-and-braces: catches a race condition that slipped past the checks above
            throw new ConflictException("This slot was just booked by someone else. Please try another.");
        }
    }

    @Transactional
    public Appointment cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment " + appointmentId + " not found"));

        // "at least 1 day in the future" -> the appointment date must be strictly after today
        if (!appointment.getDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot cancel an appointment on or after its scheduled date; "
                    + "cancellations require at least 1 day's notice");
        }

        appointmentRepository.delete(appointment);
        return appointment;
    }

    public List<String> getAvailableSlots(String doctorId, String dateStr) {
        if (doctorId == null || doctorId.isBlank()) {
            throw new ValidationException("doctor_id is required");
        }
        LocalDate date = parseDate(dateStr);

        List<Appointment> booked = appointmentRepository.findByDoctorIdAndDate(doctorId, date);
        Set<String> bookedSlots = booked.stream().map(Appointment::getTimeSlot).collect(java.util.stream.Collectors.toSet());

        List<String> available = new ArrayList<>();
        for (String slot : ALL_SLOTS) {
            if (!bookedSlots.contains(slot)) {
                available.add(slot);
            }
        }
        return available;
    }

    private void validateRequest(AppointmentRequest request) {
        if (isBlank(request.getPatientId())) {
            throw new ValidationException("patient_id is required");
        }
        if (isBlank(request.getDoctorId())) {
            throw new ValidationException("doctor_id is required");
        }
        if (isBlank(request.getDate())) {
            throw new ValidationException("date is required");
        }
        if (isBlank(request.getTimeSlot())) {
            throw new ValidationException("time_slot is required");
        }
        // reason is intentionally optional - no check here
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private LocalDate parseDate(String dateStr) {
        if (isBlank(dateStr)) {
            throw new ValidationException("date is required");
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ValidationException("date must be in yyyy-MM-dd format");
        }
    }
}
