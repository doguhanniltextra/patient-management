package com.project.Service;

import com.project.dto.request.CreateShiftRequestDto;
import com.project.dto.response.AvailabilityResponseDto;
import com.project.exception.ApiException;
import com.project.model.*;
import com.project.repository.DoctorRepository;
import com.project.repository.LeaveAbsenceRepository;
import com.project.repository.ShiftRepository;
import com.project.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorSchedulingServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private LeaveAbsenceRepository leaveAbsenceRepository;
    @Mock
    private com.project.helper.DoctorMapper doctorMapper;
    @Mock
    private com.project.helper.DoctorValidator doctorValidator;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    public void checkDoctorAvailability_WhenWithinShiftAndNoLeave_ReturnsAvailable() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Shift shift = new Shift();
        shift.setDoctorId(doctorId);
        shift.setShiftDate(LocalDate.of(2026, 11, 12));
        shift.setStartTime(LocalTime.of(8, 0));
        shift.setEndTime(LocalTime.of(16, 0));
        shift.setShiftType(ShiftType.STANDARD);
        shift.setStatus(ShiftStatus.ACTIVE);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(shiftRepository.findByDoctorIdAndShiftDateAndStatus(doctorId, LocalDate.of(2026, 11, 12), ShiftStatus.ACTIVE))
                .thenReturn(List.of(shift));
        when(leaveAbsenceRepository.existsByDoctorIdAndStatusAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
                any(), any(), any(), any())).thenReturn(false);

        AvailabilityResponseDto response = doctorService.checkDoctorAvailability(
                doctorId,
                "2026-11-12 10:00",
                "2026-11-12 10:30",
                ServiceType.CONSULTATION
        );

        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getReasonCode()).isEqualTo("IN_SHIFT");
    }

    @Test
    public void checkDoctorAvailability_WhenOnLeave_ReturnsUnavailable() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Shift shift = new Shift();
        shift.setDoctorId(doctorId);
        shift.setShiftDate(LocalDate.of(2026, 11, 12));
        shift.setStartTime(LocalTime.of(8, 0));
        shift.setEndTime(LocalTime.of(16, 0));
        shift.setStatus(ShiftStatus.ACTIVE);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(shiftRepository.findByDoctorIdAndShiftDateAndStatus(doctorId, LocalDate.of(2026, 11, 12), ShiftStatus.ACTIVE))
                .thenReturn(List.of(shift));
        when(leaveAbsenceRepository.existsByDoctorIdAndStatusAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
                any(), any(), any(), any())).thenReturn(true);

        AvailabilityResponseDto response = doctorService.checkDoctorAvailability(
                doctorId,
                "2026-11-12 10:00",
                "2026-11-12 10:30",
                ServiceType.CONSULTATION
        );

        assertThat(response.isAvailable()).isFalse();
        assertThat(response.getReasonCode()).isEqualTo("ON_LEAVE");
    }

    @Test
    public void createShift_WhenEndBeforeStart_ThrowsApiException() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        CreateShiftRequestDto dto = new CreateShiftRequestDto();
        dto.setShiftDate("2026-11-12");
        dto.setStartTime("16:00");
        dto.setEndTime("08:00");
        dto.setShiftType(ShiftType.STANDARD);

        assertThatThrownBy(() -> doctorService.createShift(doctorId, dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Shift end time must be after start time");
    }
}
