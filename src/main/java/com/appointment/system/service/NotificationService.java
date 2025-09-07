package com.appointment.system.service;

import com.appointment.system.entity.Appointment;
import com.appointment.system.entity.NotificationQueue;
import com.appointment.system.repository.NotificationQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationQueueRepository notificationQueueRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${appointment.reminder.minutes-before:5}")
    private int reminderMinutesBefore;

    public void scheduleReminder(Appointment appointment) {
        LocalDateTime reminderTime = appointment.getAppointmentDateTime().minusMinutes(reminderMinutesBefore);

        NotificationQueue notification = new NotificationQueue(
            appointment,
            NotificationQueue.NotificationType.EMAIL,
            appointment.getPatient().getEmail(),
            reminderTime
        );

        String messageContent = buildReminderMessage(appointment);
        notification.setMessageContent(messageContent);

        if (appointment.getPatient().getPhoneNumber() != null) {
            notification.setRecipientPhone(appointment.getPatient().getPhoneNumber());
        }

        notificationQueueRepository.save(notification);
    }

    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<NotificationQueue> pendingNotifications = notificationQueueRepository.findPendingNotifications(now);

        for (NotificationQueue notification : pendingNotifications) {
            try {
                switch (notification.getNotificationType()) {
                    case EMAIL:
                        sendEmailNotification(notification);
                        break;
                    case SMS:
                        sendSmsNotification(notification);
                        break;
                }
                notification.setStatus(NotificationQueue.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now()); // Fixed: changed from setSentTime to setSentAt
            } catch (Exception e) {
                notification.setStatus(NotificationQueue.NotificationStatus.FAILED);
                // Log error but continue processing other notifications
                System.err.println("Failed to send notification: " + e.getMessage());
            }
            notificationQueueRepository.save(notification);
        }
    }

    private void sendEmailNotification(NotificationQueue notification) {
        if (mailSender == null) {
            System.out.println("Email service not configured - skipping email notification");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipientEmail());
        message.setSubject("Appointment Reminder");
        message.setText(notification.getMessageContent());

        mailSender.send(message);
    }

    private void sendSmsNotification(NotificationQueue notification) {
        // SMS functionality can be implemented later
        System.out.println("SMS notification would be sent to: " + notification.getRecipientPhone());
        System.out.println("Message: " + notification.getMessageContent());
    }

    private String buildReminderMessage(Appointment appointment) {
        return String.format(
            "Dear %s,\n\nThis is a reminder that you have an appointment scheduled with Dr. %s on %s.\n\nPlease arrive 15 minutes early.\n\nThank you!",
            appointment.getPatient().getFirstName(),
            appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName(),
            appointment.getAppointmentDateTime().toString()
        );
    }
}
