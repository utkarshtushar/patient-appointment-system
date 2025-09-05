package com.appointment.system.scheduler;

import com.appointment.system.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    @Autowired
    private NotificationService notificationService;

    // Run every minute to check for appointment reminders
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void processReminders() {
        try {
            notificationService.processReminders();
        } catch (Exception e) {
            System.err.println("Error processing notification reminders: " + e.getMessage());
        }
    }
}
