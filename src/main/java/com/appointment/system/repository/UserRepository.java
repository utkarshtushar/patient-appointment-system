package com.appointment.system.repository;

import com.appointment.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(User.Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.Role role);

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.isActive = true")
    List<User> findActiveDoctors();

    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT' AND u.isActive = true")
    List<User> findActivePatients();

    @Query("SELECT u FROM User u WHERE u.specialization = :specialization AND u.role = 'DOCTOR' AND u.isActive = true")
    List<User> findDoctorsBySpecialization(@Param("specialization") String specialization);
}
