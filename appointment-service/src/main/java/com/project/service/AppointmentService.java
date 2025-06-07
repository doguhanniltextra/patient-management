package com.project.service;

import java.util.List;
import java.util.UUID;


import com.project.exception.CustomNotFoundException;
import com.project.kafka.KafkaProducer;
import com.project.utils.IdValidation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.project.model.Appointment;
import com.project.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AppointmentService {

    private final KafkaProducer kafkaProducer;
    private final AppointmentRepository appointmentRepository;
    private final IdValidation idValidation;
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    public AppointmentService(KafkaProducer kafkaProducer, AppointmentRepository appointmentRepository , IdValidation idValidation) {
        this.kafkaProducer = kafkaProducer;
        this.appointmentRepository = appointmentRepository;
        this.idValidation = idValidation;
    }


    public Appointment createAppointment(Appointment appointment) {

        log.info("Appointment starting -> {}", appointment);

        UUID patientId = appointment.getPatientId();
        UUID doctorId = appointment.getDoctorId();

        log.info("Id Validation - Check Patient Exists - {}", appointment.getPatientId());
        if (!idValidation.checkPatientExists(patientId)) {
            throw new CustomNotFoundException("Patient not found: " + patientId);
        }

        log.info("Id Validation - Check Doctor Exists - {}", appointment.getDoctorId());
        if (!idValidation.checkDoctorExists(doctorId)) {
            throw new CustomNotFoundException("Doctor not found: " + doctorId);
        }

        log.info("Kafka Process Starting");
        kafkaProducer.sendEvent(appointment);

        log.info("Save Appointment");
        return appointmentRepository.save(appointment);
    }
    public ResponseEntity<Appointment> updateAppointment(Appointment appointment) {
        log.info("Update Appointment -> {}", appointment.getId());
        Appointment existingAppointment = appointmentRepository.findById(appointment.getId()).orElse(null);
        if (existingAppointment != null) {
            existingAppointment.setPatientId(appointment.getPatientId());
            existingAppointment.setDoctorId(appointment.getDoctorId());
            existingAppointment.setServiceDate(appointment.getServiceDate());
            existingAppointment.setServiceType(appointment.getServiceType());
            existingAppointment.setAmount(appointment.getAmount());
            existingAppointment.setPaymentStatus(appointment.isPaymentStatus());
            existingAppointment.setPaymentType(appointment.getPaymentType());

            log.info("Update ended");
            return ResponseEntity.ok().body(appointmentRepository.save(existingAppointment));
        }
        return ResponseEntity.badRequest().build(); 
    }

    public void deleteAppointment(UUID id) {
        log.info("Delete Appointment ->  {}", id);
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
}
