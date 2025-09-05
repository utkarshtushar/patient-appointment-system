package com.appointment.system.queue;

import com.appointment.system.dto.request.BookAppointmentRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class AppointmentBookingConsumer {
    
    @Autowired
    private com.appointment.system.service.AppointmentService appointmentService;
    
    @RabbitListener(queues = "appointment.booking.queue")
    public void handleAppointmentBooking(BookAppointmentMessage message) {
        try {
            appointmentService.bookAppointment(
                message.getPatientId(),
                message.getSlotId(),
                message.getPatientNotes()
            );
            
            System.out.println("Successfully processed appointment booking for patient: " + message.getPatientId());
            
        } catch (Exception e) {
            System.err.println("Failed to process appointment booking: " + e.getMessage());
            // TODO: Implement dead letter queue or retry mechanism
        }
    }
    
    public static class BookAppointmentMessage {
        private Long patientId;
        private Long slotId;
        private String patientNotes;
        
        public BookAppointmentMessage() {}
        
        public BookAppointmentMessage(Long patientId, Long slotId, String patientNotes) {
            this.patientId = patientId;
            this.slotId = slotId;
            this.patientNotes = patientNotes;
        }
        
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        
        public Long getSlotId() { return slotId; }
        public void setSlotId(Long slotId) { this.slotId = slotId; }
        
        public String getPatientNotes() { return patientNotes; }
        public void setPatientNotes(String patientNotes) { this.patientNotes = patientNotes; }
    }
}
