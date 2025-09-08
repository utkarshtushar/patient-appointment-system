package com.appointment.system.repository;

import com.appointment.system.entity.AppointmentSlot;
import com.appointment.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT as FROM AppointmentSlot as WHERE as.id = :id")
    Optional<AppointmentSlot> findByIdWithLock(@Param("id") Long id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT as FROM AppointmentSlot as WHERE as.doctor = :doctor AND as.slotDateTime = :slotDateTime")
    Optional<AppointmentSlot> findByDoctorAndSlotDateTimeWithLock(@Param("doctor") User doctor, @Param("slotDateTime") LocalDateTime slotDateTime);
    
    List<AppointmentSlot> findByDoctorAndIsBookedFalseAndIsAvailableTrueAndSlotDateTimeAfter(
        User doctor, LocalDateTime currentTime);
    
    List<AppointmentSlot> findByDoctorIdAndIsBookedFalseAndIsAvailableTrueAndSlotDateTimeAfter(
        Long doctorId, LocalDateTime currentTime);
    
    @Query("SELECT as FROM AppointmentSlot as WHERE as.doctor.id = :doctorId AND as.slotDateTime BETWEEN :startDate AND :endDate AND as.isBooked = false AND as.isAvailable = true ORDER BY as.slotDateTime")
    List<AppointmentSlot> findAvailableSlotsByDoctorAndDateRange(
        @Param("doctorId") Long doctorId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT as FROM AppointmentSlot as WHERE as.slotDateTime BETWEEN :startDate AND :endDate AND as.isBooked = false AND as.isAvailable = true ORDER BY as.doctor.id, as.slotDateTime")
    List<AppointmentSlot> findAvailableSlotsByDateRange(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
    
    boolean existsByDoctorAndSlotDateTime(User doctor, LocalDateTime slotDateTime);
    
    @Query("SELECT COUNT(as) FROM AppointmentSlot as WHERE as.doctor.id = :doctorId AND as.isBooked = true")
    long countBookedSlotsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.doctor.id = :doctorId AND as.slotDateTime BETWEEN :startDate AND :endDate ORDER BY as.slotDateTime")
    List<AppointmentSlot> findAllSlotsByDoctorAndDateRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT as FROM AppointmentSlot as JOIN as.appointment a WHERE a.patient.id = :patientId AND as.slotDateTime BETWEEN :startDate AND :endDate ORDER BY as.slotDateTime")
    List<AppointmentSlot> findSlotsByPatientAndDateRange(
        @Param("patientId") Long patientId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}
