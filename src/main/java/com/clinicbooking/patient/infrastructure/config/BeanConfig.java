package com.clinicbooking.patient.infrastructure.config;

import com.clinicbooking.patient.application.port.in.DoctorUseCase;
import com.clinicbooking.patient.application.port.in.PatientUseCase;
import com.clinicbooking.patient.application.port.out.AttendanceRepositoryPort;
import com.clinicbooking.patient.application.port.out.DoctorRepositoryPort;
import com.clinicbooking.patient.application.port.out.PatientRepositoryPort;
import com.clinicbooking.patient.application.port.out.ScheduleBlockRepositoryPort;
import com.clinicbooking.patient.application.service.DoctorService;
import com.clinicbooking.patient.application.service.PatientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    DoctorUseCase doctorUseCase(DoctorRepositoryPort doctors, ScheduleBlockRepositoryPort blocks) {
        return new DoctorService(doctors, blocks);
    }

    @Bean
    PatientUseCase patientUseCase(PatientRepositoryPort patients, AttendanceRepositoryPort attendance) {
        return new PatientService(patients, attendance);
    }
}
