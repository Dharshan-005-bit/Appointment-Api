package com.clinic.appointment.controller;

import com.clinic.appointment.dto.AppointmentRequest;
import com.clinic.appointment.entity.Appointment;
import com.clinic.appointment.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointment")
    public ResponseEntity<Map<String, Object>> bookAppointment(@RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.bookAppointment(request);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("appointment_id", appointment.getAppointmentId());
        body.put("patient_id", appointment.getPatientId());
        body.put("doctor_id", appointment.getDoctorId());
        body.put("date", appointment.getDate().toString());
        body.put("time_slot", appointment.getTimeSlot());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @DeleteMapping("/appointment/{appointmentId}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable String appointmentId) {
        Appointment cancelled = appointmentService.cancelAppointment(appointmentId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Appointment cancelled");
        body.put("appointment_id", cancelled.getAppointmentId());

        return ResponseEntity.ok(body);
    }

    @GetMapping("/doctor/{doctorId}/slots")
    public ResponseEntity<Map<String, Object>> getAvailableSlots(
            @PathVariable String doctorId,
            @RequestParam String date) {

        List<String> availableSlots = appointmentService.getAvailableSlots(doctorId, date);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("doctor_id", doctorId);
        body.put("date", date);
        body.put("available_slots", availableSlots);

        return ResponseEntity.ok(body);
    }
}
