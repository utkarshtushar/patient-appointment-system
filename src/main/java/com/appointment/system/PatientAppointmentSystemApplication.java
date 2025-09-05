package com.appointment.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PatientAppointmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientAppointmentSystemApplication.class, args);
    }
}
