package com.project.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.controller.DoctorController;
import com.project.dto.UpdateDoctorControllerRequestDto;
import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.request.CreateDoctorControllerRequestDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.exception.DoctorNotFoundException;
import com.project.helper.DoctorMapper;
import com.project.helper.DoctorValidator;
import com.project.model.Doctor;
import com.project.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.http.MediaType;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static com.project.model.Specialization.Dermatologist;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.mockito.Mockito.when;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private DoctorMapper doctorMapper;

    @MockitoBean
    private DoctorValidator doctorValidator;

    @Autowired
    private ObjectMapper objectMapper;


    // --- GET ALL DOCTORS ---
    @Test
    public void DoctorController_GetDoctors_ReturnsOk() throws Exception {
        when(doctorService.getDoctors()).thenReturn(List.of(new Doctor()));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    // --- CREATE DOCTOR ---
    @Test
    public void DoctorController_CreateDoctor_ReturnsCreated() throws Exception {

        CreateDoctorControllerRequestDto controllerRequest = new CreateDoctorControllerRequestDto();
        controllerRequest.setName("Doguhan");
        controllerRequest.setEmail("doguhan@hospital.com");
        controllerRequest.setNumber("5554443322");
        controllerRequest.setHospitalName("Şehir Hastanesi");
        controllerRequest.setDepartment("Kardiyoloji");
        controllerRequest.setSpecialization(Dermatologist);
        controllerRequest.setYearsOfExperience(10);
        controllerRequest.setLicenseNumber(98765);

        CreateDoctorServiceRequestDto serviceRequest = new CreateDoctorServiceRequestDto();
        CreateDoctorServiceResponseDto serviceResponse = new CreateDoctorServiceResponseDto();
        serviceResponse.setId(UUID.randomUUID());
        serviceResponse.setName("Doguhan");
        serviceResponse.setEmail("doguhan@hospital.com");
        serviceResponse.setHospitalName("Şehir Hastanesi");


        when(doctorMapper.getCreateDoctorServiceRequestDto(any(CreateDoctorControllerRequestDto.class)))
                .thenReturn(serviceRequest);
        when(doctorService.createDoctor(any(CreateDoctorServiceRequestDto.class)))
                .thenReturn(serviceResponse);

        // 2. ACT & ASSERT
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(controllerRequest)))
                .andExpect(status().isOk()) // Controller metodun ResponseEntity.ok() döndüğü için
                .andExpect(jsonPath("$.name").value("Doguhan"))
                .andExpect(jsonPath("$.email").value("doguhan@hospital.com"));
    }

    @Test
    public void DoctorController_UpdateDoctor_ReturnsOk() throws Exception, DoctorNotFoundException {
        // --- 1. ARRANGE ---
        UUID id = UUID.randomUUID();

        UpdateDoctorControllerRequestDto requestDto = new UpdateDoctorControllerRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setEmail("updated@hospital.com");
        requestDto.setHospitalName("Yeni Hastane");
        requestDto.setSpecialization(Dermatologist);
        requestDto.setLicenseNumber(54321);
        requestDto.setDepartment("Kardiyoloji");
        requestDto.setYearsOfExperience(10);
        requestDto.setAvailable(true);
        requestDto.setPatientCount(5);

        UpdateDoctorServiceResponseDto serviceResponse = new UpdateDoctorServiceResponseDto();
        serviceResponse.setName("Updated Name");

        // Stubbing
        when(doctorService.updateDoctor(eq(id), any(UpdateDoctorServiceRequestDto.class)))
                .thenReturn(serviceResponse);

        // --- 2. ACT & ASSERT ---
        mockMvc.perform(put("/doctors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(doctorService, times(1)).updateDoctor(eq(id), any());
    }
    // --- DELETE DOCTOR ---
    @Test
    public void DoctorController_DeleteDoctor_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(doctorService).deleteDoctor(id);

        mockMvc.perform(delete("/doctors/" + id))
                .andExpect(status().isNoContent());
    }

    // --- FIND BY ID (SUCCESS) ---
    @Test
    public void DoctorController_FindDoctorById_ReturnsDoctor() throws Exception {
        UUID id = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda");

        when(doctorService.findDoctorById(id)).thenReturn(Optional.of(doctor));

        mockMvc.perform(get("/doctors/find/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Arda"));
    }

    // --- FIND BY ID (NOT FOUND) ---
    @Test
    public void DoctorController_FindDoctorById_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(doctorService.findDoctorById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/doctors/find/" + id))
                .andExpect(status().isNotFound());
    }
}