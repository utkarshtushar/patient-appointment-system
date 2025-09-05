package com.appointment.system.repository;

import com.appointment.system.entity.DoctorSchedule;
import com.appointment.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    
    List<DoctorSchedule> findByDoctorAndIsActiveTrue(User doctor);
    
    List<DoctorSchedule> findByDoctorIdAndIsActiveTrue(Long doctorId);
    
    Optional<DoctorSchedule> findByDoctorAndDayOfWeekAndIsActiveTrue(User doctor, DayOfWeek dayOfWeek);
    
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId AND ds.dayOfWeek = :dayOfWeek AND ds.isActive = true")
    Optional<DoctorSchedule> findActiveDoctorSchedule(@Param("doctorId") Long doctorId, @Param("dayOfWeek") DayOfWeek dayOfWeek);
    
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.isActive = true")
    List<DoctorSchedule> findAllActiveSchedules();
    
    boolean existsByDoctorAndDayOfWeekAndIsActiveTrue(User doctor, DayOfWeek dayOfWeek);
}
