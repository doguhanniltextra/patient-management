package com.project.controller;

import com.project.constants.Endpoints;
import com.project.dto.request.CreateLabOrderRequestDto;
import com.project.dto.response.CreateLabOrderResponseDto;
import com.project.service.DoctorLabOrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Endpoints.DOCTOR_CONTROLLER_REQUEST)
public class DoctorLabOrderController {
    private final DoctorLabOrderService doctorLabOrderService;

    public DoctorLabOrderController(DoctorLabOrderService doctorLabOrderService) {
        this.doctorLabOrderService = doctorLabOrderService;
    }

    @PostMapping(Endpoints.DOCTOR_CONTROLLER_LAB_ORDERS)
    @PreAuthorize("hasRole('ADMIN') or @securityService.isDoctorOwner(authentication, #doctorId)")
    public CreateLabOrderResponseDto createLabOrder(@PathVariable UUID doctorId, @Valid @RequestBody CreateLabOrderRequestDto request) {
        return doctorLabOrderService.placeOrder(doctorId, request);
    }
}
