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

    @Autowired
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
                sendNotification(notification);
                notification.setStatus(NotificationQueue.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
            } catch (Exception e) {
                notification.setStatus(NotificationQueue.NotificationStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
                notification.setRetryCount(notification.getRetryCount() + 1);
            }
            notificationQueueRepository.save(notification);
        }
    }

    private void sendNotification(NotificationQueue notification) {
        switch (notification.getNotificationType()) {
            case EMAIL:
                sendEmailNotification(notification);
                break;
            case SMS:
                sendSMSNotification(notification);
                break;
            case PUSH_NOTIFICATION:
                sendPushNotification(notification);
                break;
        }
    }

    private void sendEmailNotification(NotificationQueue notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipientEmail());
        message.setSubject("Appointment Reminder");
        message.setText(notification.getMessageContent());

        mailSender.send(message);
    }

    private void sendSMSNotification(NotificationQueue notification) {
        // TODO: Implement SMS sending using Twilio or similar service
        // For now, just log the message
        System.out.println("SMS to " + notification.getRecipientPhone() + ": " + notification.getMessageContent());
    }

    private void sendPushNotification(NotificationQueue notification) {
        // TODO: Implement push notification using Firebase or similar service
        System.out.println("Push notification: " + notification.getMessageContent());
    }

    private String buildReminderMessage(Appointment appointment) {
        return String.format(
            "Dear %s %s,\n\n" +
            "This is a reminder that you have an appointment with Dr. %s %s on %s.\n\n" +
            "Appointment Details:\n" +
            "Date & Time: %s\n" +
            "Doctor: Dr. %s %s\n" +
            "Specialization: %s\n\n" +
            "Please arrive 15 minutes early for your appointment.\n\n" +
            "Best regards,\n" +
            "Patient Appointment System",
            appointment.getPatient().getFirstName(),
            appointment.getPatient().getLastName(),
            appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getLastName(),
            appointment.getAppointmentDateTime().toLocalDate(),
            appointment.getAppointmentDateTime(),
            appointment.getDoctor().getFirstName(),
            appointment.getDoctor().getLastName(),
            appointment.getDoctor().getSpecialization() != null ? appointment.getDoctor().getSpecialization() : "General"
        );
    }
}
