package com.project.patient_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.patient_service.controller.MedicalRecordController;
import com.project.patient_service.dto.CreateMedicalRecordRequestDto;
import com.project.patient_service.dto.MedicalRecordResponseDto;
import com.project.patient_service.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void createRecord_ReturnsOk() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();

        CreateMedicalRecordRequestDto request = new CreateMedicalRecordRequestDto();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setAppointmentId(appointmentId);
        request.setDiagnosis("Common Cold");

        MedicalRecordResponseDto response = new MedicalRecordResponseDto();
        response.setId(UUID.randomUUID());
        response.setPatientId(patientId);
        response.setDiagnosis("Common Cold");

        when(medicalRecordService.createMedicalRecord(any(CreateMedicalRecordRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/patients/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Common Cold"))
                .andExpect(jsonPath("$.patientId").value(patientId.toString()));

        verify(medicalRecordService, times(1)).createMedicalRecord(any(CreateMedicalRecordRequestDto.class));
    }

    @Test
    public void getRecordsByPatient_ReturnsOk() throws Exception {
        UUID patientId = UUID.randomUUID();

        MedicalRecordResponseDto res1 = new MedicalRecordResponseDto();
        res1.setId(UUID.randomUUID());
        res1.setPatientId(patientId);
        res1.setDiagnosis("Migraine");

        MedicalRecordResponseDto res2 = new MedicalRecordResponseDto();
        res2.setId(UUID.randomUUID());
        res2.setPatientId(patientId);
        res2.setDiagnosis("Back Pain");

        when(medicalRecordService.getRecordsByPatient(patientId)).thenReturn(List.of(res1, res2));

        mockMvc.perform(get("/patients/records/patient/" + patientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].diagnosis").value("Migraine"))
                .andExpect(jsonPath("$[1].diagnosis").value("Back Pain"));

        verify(medicalRecordService, times(1)).getRecordsByPatient(patientId);
    }
}
