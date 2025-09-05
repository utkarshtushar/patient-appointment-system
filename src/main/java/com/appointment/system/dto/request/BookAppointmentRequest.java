package com.appointment.system.dto.request;

import jakarta.validation.constraints.NotNull;

public class BookAppointmentRequest {
    
    @NotNull
    private Long slotId;
    
    private String patientNotes;
    
    public BookAppointmentRequest() {}
    
    public BookAppointmentRequest(Long slotId, String patientNotes) {
        this.slotId = slotId;
        this.patientNotes = patientNotes;
    }
    
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    
    public String getPatientNotes() { return patientNotes; }
    public void setPatientNotes(String patientNotes) { this.patientNotes = patientNotes; }
}
