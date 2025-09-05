package com.appointment.system.repository;

import com.appointment.system.entity.Appointment;
import com.appointment.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientAndStatusNot(User patient, Appointment.AppointmentStatus status);
    
    List<Appointment> findByDoctorAndStatusNot(User doctor, Appointment.AppointmentStatus status);
    
    List<Appointment> findByPatientIdAndStatusNot(Long patientId, Appointment.AppointmentStatus status);
    
    List<Appointment> findByDoctorIdAndStatusNot(Long doctorId, Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :startDate AND :endDate AND a.status = :status")
    List<Appointment> findByDateRangeAndStatus(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("status") Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :startTime AND :endTime AND a.reminderSent = false AND a.status = 'CONFIRMED'")
    List<Appointment> findAppointmentsForReminder(
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime")
    List<Appointment> findByDoctorAndDateRange(
        @Param("doctorId") Long doctorId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime")
    List<Appointment> findByPatientAndDateRange(
        @Param("patientId") Long patientId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
    
    Optional<Appointment> findByAppointmentSlotId(Long appointmentSlotId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'CONFIRMED'")
    long countConfirmedAppointmentsByDoctor(@Param("doctorId") Long doctorId);
}
