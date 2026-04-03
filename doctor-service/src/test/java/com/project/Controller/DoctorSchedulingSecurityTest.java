package com.project.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.controller.DoctorController;
import com.project.dto.request.CreateLeaveRequestDto;
import com.project.dto.request.CreateShiftRequestDto;
import com.project.model.LeaveType;
import com.project.model.ServiceType;
import com.project.model.ShiftType;
import com.project.security.InternalServiceAuthFilter;
import com.project.security.JwtAuthFilter;
import com.project.security.SecurityConfig;
import com.project.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DoctorController.class)
@Import(SecurityConfig.class)
public class DoctorSchedulingSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private com.project.helper.DoctorMapper doctorMapper;

    @MockBean
    private com.project.helper.DoctorValidator doctorValidator;

    @MockBean
    private com.project.security.SecurityOwnershipService securityOwnershipService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private InternalServiceAuthFilter internalServiceAuthFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createLeave_WithAdminRole_ReturnsOk() throws Exception {
        CreateLeaveRequestDto dto = new CreateLeaveRequestDto();
        dto.setLeaveType(LeaveType.VACATION);
        dto.setStartDateTime("2026-11-12 09:00");
        dto.setEndDateTime("2026-11-12 18:00");

        when(doctorService.createLeave(any(), any())).thenReturn(new com.project.dto.response.LeaveResponseDto());

        mockMvc.perform(post("/doctors/{doctorId}/leaves", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    public void availability_WithPatientRole_ReturnsOk() throws Exception {
        when(doctorService.checkDoctorAvailability(any(), anyString(), anyString(), eq(ServiceType.CONSULTATION)))
                .thenReturn(new com.project.dto.response.AvailabilityResponseDto(true, "IN_SHIFT", "ok"));

        mockMvc.perform(get("/doctors/{doctorId}/availability", UUID.randomUUID())
                        .param("start", "2026-11-12 10:00")
                        .param("end", "2026-11-12 10:30")
                        .param("serviceType", "CONSULTATION"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    public void bulkAvailability_WithReceptionistRole_ReturnsOk() throws Exception {
        when(doctorService.findAvailableDoctorsForSlot(anyString(), anyString(), eq(ServiceType.CONSULTATION), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/doctors/availability")
                        .param("start", "2026-11-12 10:00")
                        .param("end", "2026-11-12 10:30")
                        .param("serviceType", "CONSULTATION")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }
}
