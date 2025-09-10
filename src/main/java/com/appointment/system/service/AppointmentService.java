package com.appointment.system.service;

import com.appointment.system.entity.Appointment;
import com.appointment.system.entity.AppointmentSlot;
import com.appointment.system.entity.User;
import com.appointment.system.repository.AppointmentRepository;
import com.appointment.system.repository.AppointmentSlotRepository;
import com.appointment.system.repository.UserRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private RedissonClient redissonClient;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Appointment bookAppointment(Long patientId, Long slotId, String patientNotes) {
        // Use Redis locking only if RedissonClient is available
        if (redissonClient != null) {
            return bookAppointmentWithRedisLock(patientId, slotId, patientNotes);
        } else {
            return bookAppointmentWithoutRedis(patientId, slotId, patientNotes);
        }
    }

    private Appointment bookAppointmentWithRedisLock(Long patientId, Long slotId, String patientNotes) {
        String lockKey = "appointment_slot_" + slotId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Try to acquire lock with 10 seconds timeout
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    return performBooking(patientId, slotId, patientNotes);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Unable to acquire lock for booking. Please try again.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Booking process was interrupted");
        }
    }

    private Appointment bookAppointmentWithoutRedis(Long patientId, Long slotId, String patientNotes) {
        // For local testing without Redis - use database-level locking
        synchronized (this) {
            return performBooking(patientId, slotId, patientNotes);
        }
    }

    private Appointment performBooking(Long patientId, Long slotId, String patientNotes) {
        // Get the slot with pessimistic lock
        AppointmentSlot slot = appointmentSlotRepository.findByIdWithLock(slotId)
                .orElseThrow(() -> new RuntimeException("Appointment slot not found"));

        // Check if slot is available
        if (slot.getIsBooked() || !slot.getIsAvailable()) {
            throw new RuntimeException("Appointment slot is not available");
        }

        // Load the patient user from database to ensure all fields are populated
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Mark slot as booked
        slot.setIsBooked(true);
        appointmentSlotRepository.save(slot);

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);  // Set the fully loaded patient object
        appointment.setDoctor(slot.getDoctor());
        appointment.setAppointmentSlot(slot);
        appointment.setAppointmentDateTime(slot.getSlotDateTime());
        appointment.setPatientNotes(patientNotes);
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Schedule reminder notification
        notificationService.scheduleReminder(savedAppointment);

        return savedAppointment;
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdAndStatusNot(patientId, Appointment.AppointmentStatus.CANCELLED);
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndStatusNot(doctorId, Appointment.AppointmentStatus.CANCELLED);
    }

    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        // Free up the slot - ensure both flags are set correctly
        AppointmentSlot slot = appointment.getAppointmentSlot();
        slot.setIsBooked(false);
        slot.setIsAvailable(true);  // Explicitly set to available
        appointmentSlotRepository.save(slot);

        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> getAppointmentsForReminder(LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.findAppointmentsForReminder(startTime, endTime);
    }
}
