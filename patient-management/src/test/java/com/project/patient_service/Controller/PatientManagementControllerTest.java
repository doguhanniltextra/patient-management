package com.project.patient_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.patient_service.controller.PatientController;
import com.project.patient_service.dto.request.CreatePatientControllerRequestDto;
import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.request.UpdatePatientControllerRequestDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.dto.response.*;
import com.project.patient_service.helper.UserMapper;
import com.project.patient_service.helper.UserValidator;
import com.project.patient_service.model.Patient;
import com.project.patient_service.service.PatientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
// BU STATIC IMPORTLAR ŞART:
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
public class PatientManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserValidator userValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void PatientController_GetPatients_ReturnsOk() throws Exception {
        // --- ARRANGE ---
        List<GetPatientServiceResponseDto> serviceResponse = List.of();
        List<GetPatientControllerResponseDto> controllerResponse = List.of();

        when(patientService.getPatients()).thenReturn(serviceResponse);
        when(userMapper.getGetPatientControllerResponseDtos(any())).thenReturn(controllerResponse);


        mockMvc.perform(get("/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void PatientController_GetPatients_WhenServiceFails_Returns500() throws Exception {
        when(patientService.getPatients()).thenThrow(new RuntimeException("Database down"));

        assertThatThrownBy(() -> mockMvc.perform(get("/patients")))
                .hasCauseInstanceOf(RuntimeException.class);
    }


    @Test
    public void PatientController_CreatePatient_ReturnsOk() throws Exception {

        CreatePatientControllerRequestDto controllerRequest = new CreatePatientControllerRequestDto();
        controllerRequest.setAddress("mock address");
        controllerRequest.setEmail("mock@gmail.com");
        controllerRequest.setDateOfBirth("10/10/2003");
        controllerRequest.setRegisteredDate("10/10/2003");
        controllerRequest.setName("mock");

        CreatePatientServiceRequestDto serviceRequest = new CreatePatientServiceRequestDto();
        serviceRequest.setName("mock");

        CreatePatientServiceResponseDto serviceResponse = new CreatePatientServiceResponseDto();
        serviceResponse.setEmail("mock@gmail.com");
        serviceResponse.setName("mock");

        when(userMapper.getCreatePatientServiceRequestDto(any(CreatePatientControllerRequestDto.class)))
                .thenReturn(serviceRequest);

        when(patientService.createPatient(any(CreatePatientServiceRequestDto.class)))
                .thenReturn(serviceResponse);


        mockMvc.perform(MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(controllerRequest)))
                .andExpect(status().isOk()) // Controller'da ResponseEntity.ok() dönüyorsan kalsın, yoksa isCreated() yap.
                .andExpect(jsonPath("$.name").value("mock"))
                .andExpect(jsonPath("$.email").value("mock@gmail.com"));
    }


    @Test
    public void PatientController_UpdatePatient_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        UpdatePatientControllerRequestDto requestDto = new UpdatePatientControllerRequestDto();
        requestDto.setName("Updated Name");

        UpdatePatientServiceResponseDto serviceResponse = new UpdatePatientServiceResponseDto();
        UpdatePatientControllerResponseDto controllerResponse = new UpdatePatientControllerResponseDto();
        controllerResponse.setName("Updated Name");

        when(userMapper.getUpdatePatientServiceRequestDto(any())).thenReturn(new UpdatePatientServiceRequestDto());
        when(patientService.updatePatient(eq(id), any())).thenReturn(serviceResponse);
        when(userMapper.getUpdatePatientControllerResponseDto(any())).thenReturn(controllerResponse);

        mockMvc.perform(put("/patients/" + id) // PUT isteği
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }


    @Test
    public void PatientController_DeletePatient_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        // void dönen metotlar için doNothing kullanılır (zaten mock nesnelerde varsayılandır)
        doNothing().when(patientService).deletePatient(id);

        mockMvc.perform(delete("/patients/" + id)) // DELETE isteği
                .andExpect(status().isNoContent()); // 204 No Content bekliyoruz
    }


    @Test
    public void PatientController_FindPatientById_ReturnsPatient() throws Exception {
        UUID id = UUID.randomUUID();
        Patient mockPatient = new Patient();
        mockPatient.setId(id);

        when(patientService.findPatientById(id)).thenReturn(Optional.of(mockPatient));
        // Validator'ın ResponseEntity döndüren metodunu mockluyoruz
        when(userValidator.getPatientResponseEntity(any())).thenReturn(ResponseEntity.ok(mockPatient));

        mockMvc.perform(get("/patients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    // --- FIND BY EMAIL TEST ---
    @Test
    public void PatientController_FindPatientByEmail_ReturnsBoolean() throws Exception {
        String email = "test@gmail.com";

        // Controller'daki mantığa göre validator'ı mockluyoruz
        when(userValidator.isPatientByEmail(eq(email), any())).thenReturn(true);
        when(userValidator.getBooleanResponseEntity(true)).thenReturn(ResponseEntity.ok(true));

        mockMvc.perform(get("/patients/email/" + email)) // Path'i Endpoints sınıfındaki tanıma göre kontrol et
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}

