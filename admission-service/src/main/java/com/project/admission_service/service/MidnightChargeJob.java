package com.project.admission_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admission_service.dto.DailyBedChargeEvent;
import com.project.admission_service.model.*;
import com.project.admission_service.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MidnightChargeJob {
    private static final Logger log = LoggerFactory.getLogger(MidnightChargeJob.class);

    private final AdmissionRepository admissionRepository;
    private final WardRepository wardRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final AdmissionOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public MidnightChargeJob(
            AdmissionRepository admissionRepository,
            WardRepository wardRepository,
            RoomRepository roomRepository,
            BedRepository bedRepository,
            AdmissionOutboxRepository outboxRepository,
            ObjectMapper objectMapper) {
        this.admissionRepository = admissionRepository;
        this.wardRepository = wardRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    // Cron expression for midnight: "0 0 0 * * ?"
    // Using a simpler fixedDelay for testing/development if needed, but cron is better for requirements.
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void generateDailyCharges() {
        log.info("Starting midnight bed charge generation job...");
        List<Admission> activeAdmissions = admissionRepository.findByStatus(AdmissionStatus.ACTIVE);

        for (Admission admission : activeAdmissions) {
            try {
                // Get ward rate for the admission
                Bed bed = bedRepository.findById(admission.getBedId()).get();
                Room room = roomRepository.findById(bed.getRoomId()).get();
                Ward ward = wardRepository.findById(room.getWardId()).get();

                DailyBedChargeEvent event = new DailyBedChargeEvent();
                event.setEventId(UUID.randomUUID().toString());
                event.setPatientId(admission.getPatientId());
                event.setAdmissionId(admission.getId());
                event.setAmount(ward.getDailyRate());
                event.setCurrency("TRY");

                AdmissionOutboxEvent outboxEvent = new AdmissionOutboxEvent();
                outboxEvent.setAggregateType("ADMISSION");
                outboxEvent.setAggregateId(admission.getId().toString());
                outboxEvent.setEventType("DAILY_BED_CHARGE");
                outboxEvent.setPayloadJson(objectMapper.writeValueAsString(event));
                outboxEvent.setStatus("PENDING");
                outboxEvent.setRetryCount(0);
                outboxEvent.setCreatedAt(Instant.now());
                outboxRepository.save(outboxEvent);

                log.debug("Outbox charge event created for admission: {}", admission.getId());
            } catch (JsonProcessingException e) {
                log.error("Error creating daily charge JSON for admission: {}", admission.getId(), e);
            }
        }
        log.info("Midnight bed charge generation job completed for {} admissions.", activeAdmissions.size());
    }
}
