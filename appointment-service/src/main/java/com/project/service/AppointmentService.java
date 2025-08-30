package com.project.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import com.project.constants.LogMessages;
import com.project.dto.AppointmentDTO;
import com.project.dto.AppointmentKafkaResponseDto;
import com.project.dto.request.CreateAppointmentServiceRequestDto;
import com.project.dto.response.CreateAppointmentServiceResponseDto;
import com.project.exception.CustomNotFoundException;
import com.project.helper.AppointmentMapper;
import com.project.helper.AppointmentValidator;
import com.project.kafka.KafkaProducer;
import com.project.utils.IdValidation;

import jakarta.transaction.Transactional;
import org.apache.juli.logging.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final AppointmentMapper appointmentMapper;
    private final AppointmentValidator appointmentValidator;

    public AppointmentService(KafkaProducer kafkaProducer, AppointmentRepository appointmentRepository , IdValidation idValidation, AppointmentMapper appointmentMapper, AppointmentValidator appointmentValidator) {
        this.kafkaProducer = kafkaProducer;
        this.appointmentRepository = appointmentRepository;
        this.idValidation = idValidation;
        this.appointmentMapper = appointmentMapper;
        this.appointmentValidator = appointmentValidator;
    }

    public CreateAppointmentServiceResponseDto createAppointment(CreateAppointmentServiceRequestDto createAppointmentServiceRequestDto) {

        log.info(LogMessages.SERVICE_CREATE_SAVING, createAppointmentServiceRequestDto);
        UUID patientId = createAppointmentServiceRequestDto.getPatientId();
        UUID doctorId = createAppointmentServiceRequestDto.getDoctorId();

        AppointmentValidator.Result result = new AppointmentValidator.Result(patientId, doctorId);

        log.info(LogMessages.SERVICE_CREATE_VALIDATE_PATIENT, createAppointmentServiceRequestDto.getPatientId());
        appointmentValidator.checkPatientExistsOrNotForCreateAppointment(result.patientId());

        log.info(LogMessages.SERVICE_CREATE_VALIDATE_DOCTOR, createAppointmentServiceRequestDto.getDoctorId());
        appointmentValidator.checkDoctorExistsOrNotForCreateAppointment(result.doctorId());

        Appointment appointment = appointmentMapper.getAppointment(createAppointmentServiceRequestDto);
        CreateAppointmentServiceResponseDto appointmentServiceResponseDto = appointmentMapper.getCreateAppointmentServiceResponseDto(createAppointmentServiceRequestDto);

        appointmentRepository.save(appointment);

        return appointmentServiceResponseDto;
    }

    public ResponseEntity<Appointment> updateAppointment(Appointment appointment) {
        log.info(LogMessages.SERVICE_UPDATE_STARTING, appointment.getId());
        Appointment existingAppointment = appointmentRepository.findById(appointment.getId()).orElse(null);
        if (existingAppointment != null) {
            appointmentMapper.updateAppointmentExtracted(appointment, existingAppointment);
            log.info(LogMessages.SERVICE_UPDATE_ENDED);
            return ResponseEntity.ok().body(appointmentRepository.save(existingAppointment));
        }
        return ResponseEntity.badRequest().build(); 
    }

    public void deleteAppointment(UUID id) {
        log.info(LogMessages.SERVICE_DELETE_TRIGGERED, id);
        appointmentRepository.deleteById(id);
    }

    public void updatePaymentStatus(UUID id, boolean status) {
        log.info(LogMessages.SERVICE_UPDATE_PAYMENT_STATUS_TRIGGERED);

        Appointment appointment = appointmentValidator.getAppointmentForUpdatePaymentStatus(id, appointmentRepository);

        appointment.setPaymentStatus(status);
        appointmentRepository.save(appointment);

        AppointmentKafkaResponseDto appointmentDTO = appointmentValidator.getAppointmentKafkaResponseDto(status, appointment);
        appointmentValidator.updatePaymentStatusKafkaSendEvent(status, appointmentDTO, kafkaProducer);
    }

    public List<Appointment> getAllAppointments() {
        log.info(LogMessages.SERVICE_GET_ALL_TRIGGERED);
        return appointmentRepository.findAll();
    }
    
}
