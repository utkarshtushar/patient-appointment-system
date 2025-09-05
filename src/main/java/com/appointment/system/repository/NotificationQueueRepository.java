package com.appointment.system.repository;

import com.appointment.system.entity.NotificationQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, Long> {
    
    @Query("SELECT nq FROM NotificationQueue nq WHERE nq.scheduledTime <= :currentTime AND nq.status = 'PENDING'")
    List<NotificationQueue> findPendingNotifications(@Param("currentTime") LocalDateTime currentTime);
    
    List<NotificationQueue> findByStatusAndRetryCountLessThan(
        NotificationQueue.NotificationStatus status, 
        Integer maxRetries);
    
    @Query("SELECT nq FROM NotificationQueue nq WHERE nq.appointment.id = :appointmentId")
    List<NotificationQueue> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT nq FROM NotificationQueue nq WHERE nq.status = 'FAILED' AND nq.retryCount < nq.maxRetries")
    List<NotificationQueue> findFailedNotificationsForRetry();
}
