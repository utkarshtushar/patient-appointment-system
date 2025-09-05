package com.appointment.system.service;

import com.appointment.system.entity.AppointmentSlot;
import com.appointment.system.entity.DoctorSchedule;
import com.appointment.system.repository.AppointmentSlotRepository;
import com.appointment.system.repository.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotGenerationService {

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    // Fixed to 30 days for 1-month scheduling
    private final int daysAhead = 30;

    @Transactional
    public void generateSlotsForDoctor(DoctorSchedule schedule) {
        LocalDate startDate = LocalDate.now().plusDays(1); // Start from tomorrow
        LocalDate endDate = startDate.plusDays(daysAhead); // Exactly 30 days ahead

        List<AppointmentSlot> slotsToCreate = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == schedule.getDayOfWeek()) {
                List<AppointmentSlot> dailySlots = generateDailySlots(schedule, date);
                slotsToCreate.addAll(dailySlots);
            }
        }

        // Batch save all slots
        appointmentSlotRepository.saveAll(slotsToCreate);
        System.out.println("Generated " + slotsToCreate.size() + " slots for doctor " +
                          schedule.getDoctor().getFirstName() + " " + schedule.getDoctor().getLastName() +
                          " for " + schedule.getDayOfWeek());
    }

    private List<AppointmentSlot> generateDailySlots(DoctorSchedule schedule, LocalDate date) {
        List<AppointmentSlot> slots = new ArrayList<>();
        LocalTime currentTime = schedule.getStartTime();

        while (currentTime.isBefore(schedule.getEndTime())) {
            // Skip break time if configured
            if (isInBreakTime(currentTime, schedule)) {
                currentTime = currentTime.plusMinutes(schedule.getSlotDurationMinutes());
                continue;
            }

            LocalDateTime slotDateTime = LocalDateTime.of(date, currentTime);

            // Check if slot already exists
            if (!appointmentSlotRepository.existsByDoctorAndSlotDateTime(schedule.getDoctor(), slotDateTime)) {
                AppointmentSlot slot = new AppointmentSlot(schedule.getDoctor(), slotDateTime);
                slots.add(slot);
            }

            currentTime = currentTime.plusMinutes(schedule.getSlotDurationMinutes());
        }

        return slots;
    }

    private boolean isInBreakTime(LocalTime currentTime, DoctorSchedule schedule) {
        if (schedule.getBreakStartTime() != null && schedule.getBreakEndTime() != null) {
            return !currentTime.isBefore(schedule.getBreakStartTime()) &&
                   currentTime.isBefore(schedule.getBreakEndTime());
        }
        return false;
    }

    public List<AppointmentSlot> getAvailableSlots(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return appointmentSlotRepository.findAvailableSlotsByDoctorAndDateRange(
            doctorId, startDateTime, endDateTime);
    }
}
