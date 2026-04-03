package com.project.service;

import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.request.CreateLeaveRequestDto;
import com.project.dto.request.CreateShiftRequestDto;
import com.project.dto.response.AvailabilityResponseDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.dto.response.DoctorAvailabilitySummaryDto;
import com.project.dto.response.LeaveResponseDto;
import com.project.dto.response.ShiftResponseDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.ApiException;
import com.project.exception.DoctorNotFoundException;
import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.PatientLimitException;
import com.project.helper.DoctorMapper;
import com.project.helper.DoctorValidator;
import com.project.model.Doctor;
import com.project.model.LeaveAbsence;
import com.project.model.LeaveStatus;
import com.project.model.ServiceType;
import com.project.model.Shift;
import com.project.model.ShiftStatus;
import com.project.model.Specialization;
import com.project.repository.DoctorRepository;
import com.project.repository.LeaveAbsenceRepository;
import com.project.repository.ShiftRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final ShiftRepository shiftRepository;
    private final LeaveAbsenceRepository leaveAbsenceRepository;

    private final DoctorMapper doctorMapper;
    private final DoctorValidator doctorValidator;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public DoctorService(DoctorRepository doctorRepository, ShiftRepository shiftRepository, LeaveAbsenceRepository leaveAbsenceRepository, DoctorMapper doctorMapper, DoctorValidator doctorValidator) {
        this.doctorRepository = doctorRepository;
        this.shiftRepository = shiftRepository;
        this.leaveAbsenceRepository = leaveAbsenceRepository;
        this.doctorMapper = doctorMapper;
        this.doctorValidator = doctorValidator;
    }

    public UpdateDoctorServiceResponseDto updateDoctor(UUID id, UpdateDoctorServiceRequestDto updateDoctorServiceRequestDto) throws DoctorNotFoundException {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);

        if (optionalDoctor.isPresent()) {
            Doctor existingDoctor = doctorMapper.getDoctorRequestDto(updateDoctorServiceRequestDto, optionalDoctor);
            doctorRepository.save(existingDoctor);

            UpdateDoctorServiceResponseDto updateDoctorServiceResponseDto = doctorMapper.getUpdateDoctorServiceResponseDto(existingDoctor);
            return updateDoctorServiceResponseDto;
        } else {
            throw new DoctorNotFoundException("Doctor with id " + id + " not found.");
        }
    }

    @Transactional
    public CreateDoctorServiceResponseDto createDoctor(@Valid CreateDoctorServiceRequestDto createDoctorServiceRequestDto) throws EmailIsNotUniqueException {
        doctorValidator.checkEmailIsUniqueOrNotForCreate(createDoctorServiceRequestDto, doctorRepository);

        Doctor doctor = doctorMapper.toEntity(createDoctorServiceRequestDto);
        Doctor result = doctorRepository.save(doctor);

        return doctorMapper.toCreateDoctorServiceResponseDto(result);
    }


    public void deleteDoctor(UUID id) {
        doctorRepository.deleteById(id);
    }
    public Optional<Doctor> findDoctorById(UUID id) {
        Optional<Doctor> byId = doctorRepository.findById(id);
        return byId;
    }
    public Page<Doctor> getDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }
    // MAINTAINING
    public void increasePatientNumber(UUID id) throws PatientLimitException, DoctorNotFoundException {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + id));
        if (doctor.getPatientCount() >= 5) throw new PatientLimitException("Patient limit is full");
        doctor.setPatientCount(doctor.getPatientCount() + 1);
        doctorRepository.save(doctor);
    }

    public ShiftResponseDto createShift(UUID doctorId, CreateShiftRequestDto requestDto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        LocalDate shiftDate = parseDate(requestDto.getShiftDate());
        LocalTime startTime = parseTime(requestDto.getStartTime());
        LocalTime endTime = parseTime(requestDto.getEndTime());
        if (!endTime.isAfter(startTime)) {
            throw new ApiException("INVALID_SLOT", "Shift end time must be after start time", 400);
        }

        Shift shift = doctorMapper.toShift(doctor.getId(), requestDto, shiftDate, startTime, endTime);
        shiftRepository.save(shift);

        return doctorMapper.toShiftResponseDto(shift);
    }

    public List<ShiftResponseDto> listShifts(UUID doctorId, LocalDate fromDate, LocalDate toDate) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        return shiftRepository.findByDoctorIdAndShiftDateBetweenAndStatus(doctorId, fromDate, toDate, ShiftStatus.ACTIVE)
                .stream()
                .map(doctorMapper::toShiftResponseDto)
                .toList();
    }

    @Transactional
    public void deleteShift(UUID doctorId, UUID shiftId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ApiException("SHIFT_NOT_FOUND", "Shift not found: " + shiftId, 404));

        if (!shift.getDoctorId().equals(doctorId)) {
            throw new ApiException("SHIFT_NOT_FOUND", "Shift not found for doctor", 404);
        }

        shift.setStatus(ShiftStatus.CANCELLED);
        shiftRepository.save(shift);
    }

    public LeaveResponseDto createLeave(UUID doctorId, CreateLeaveRequestDto requestDto) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        LocalDateTime startDateTime = parseDateTime(requestDto.getStartDateTime());
        LocalDateTime endDateTime = parseDateTime(requestDto.getEndDateTime());
        if (!endDateTime.isAfter(startDateTime)) {
            throw new ApiException("INVALID_SLOT", "Leave end datetime must be after start datetime", 400);
        }

        LeaveAbsence leaveAbsence = doctorMapper.toLeaveAbsence(doctorId, requestDto, startDateTime, endDateTime);

        leaveAbsenceRepository.save(leaveAbsence);
        return doctorMapper.toLeaveResponseDto(leaveAbsence);
    }

    @Transactional
    public LeaveResponseDto approveLeave(UUID doctorId, UUID leaveId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
        LeaveAbsence leaveAbsence = leaveAbsenceRepository.findById(leaveId)
                .orElseThrow(() -> new ApiException("LEAVE_NOT_FOUND", "Leave not found: " + leaveId, 404));

        if (!leaveAbsence.getDoctorId().equals(doctorId)) {
            throw new ApiException("LEAVE_NOT_FOUND", "Leave not found for doctor", 404);
        }

        leaveAbsence.setStatus(LeaveStatus.APPROVED);
        leaveAbsenceRepository.save(leaveAbsence);
        return doctorMapper.toLeaveResponseDto(leaveAbsence);
    }

    @Transactional
    public void deleteLeave(UUID doctorId, UUID leaveId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));
        LeaveAbsence leaveAbsence = leaveAbsenceRepository.findById(leaveId)
                .orElseThrow(() -> new ApiException("LEAVE_NOT_FOUND", "Leave not found: " + leaveId, 404));

        if (!leaveAbsence.getDoctorId().equals(doctorId)) {
            throw new ApiException("LEAVE_NOT_FOUND", "Leave not found for doctor", 404);
        }

        leaveAbsence.setStatus(LeaveStatus.CANCELLED);
        leaveAbsenceRepository.save(leaveAbsence);
    }

    public AvailabilityResponseDto checkDoctorAvailability(UUID doctorId, String start, String end, ServiceType serviceType) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + doctorId));

        LocalDateTime startDateTime = parseDateTime(start);
        LocalDateTime endDateTime = parseDateTime(end);

        if (!endDateTime.isAfter(startDateTime)) {
            throw new ApiException("INVALID_SLOT", "Appointment end must be after start", 400);
        }

        if (!isWithinShift(doctorId, startDateTime, endDateTime)) {
            return new AvailabilityResponseDto(false, "OUTSIDE_SHIFT", "Requested slot is outside active shift hours");
        }

        if (hasLeaveConflict(doctorId, startDateTime, endDateTime)) {
            return new AvailabilityResponseDto(false, "ON_LEAVE", "Doctor is on approved leave");
        }

        if (serviceType == ServiceType.SURGERY && hasSurgeryConflict(doctorId, startDateTime, endDateTime)) {
            return new AvailabilityResponseDto(false, "SURGERY_CONFLICT", "Surgery slot conflicts with surgery shift");
        }

        return new AvailabilityResponseDto(true, "IN_SHIFT", "Doctor is available for requested slot");
    }

    public Page<DoctorAvailabilitySummaryDto> findAvailableDoctorsForSlot(String start, String end, ServiceType serviceType, String specialization, Pageable pageable) {
        LocalDateTime startDateTime = parseDateTime(start);
        LocalDateTime endDateTime = parseDateTime(end);

        if (!endDateTime.isAfter(startDateTime)) {
            throw new ApiException("INVALID_SLOT", "Appointment end must be after start", 400);
        }

        Specialization specializationFilter = null;
        if (specialization != null && !specialization.isBlank()) {
            try {
                specializationFilter = Specialization.valueOf(specialization);
            } catch (IllegalArgumentException ex) {
                throw new ApiException("INVALID_SPECIALIZATION", "Invalid specialization value", 400);
            }
        }

        final Specialization finalSpecializationFilter = specializationFilter;
        return doctorRepository.findAll(pageable)
                .map(doctor -> {
                    boolean specializationMatches = finalSpecializationFilter == null || doctor.getSpecialization() == finalSpecializationFilter;
                    boolean available = false;
                    if (specializationMatches) {
                        AvailabilityResponseDto result = checkDoctorAvailability(
                                doctor.getId(),
                                startDateTime.format(DATE_TIME_FORMATTER),
                                endDateTime.format(DATE_TIME_FORMATTER),
                                serviceType
                        );
                        available = result.isAvailable();
                    }

                    return doctorMapper.toDoctorAvailabilitySummaryDto(doctor, available);
                });
    }

    private boolean isWithinShift(UUID doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate shiftDate = startDateTime.toLocalDate();
        if (!shiftDate.equals(endDateTime.toLocalDate())) {
            return false;
        }
        List<Shift> shifts = shiftRepository.findByDoctorIdAndShiftDateAndStatus(doctorId, shiftDate, ShiftStatus.ACTIVE);
        return shifts.stream().anyMatch(shift ->
                !startDateTime.toLocalTime().isBefore(shift.getStartTime())
                        && !endDateTime.toLocalTime().isAfter(shift.getEndTime()));
    }

    private boolean hasLeaveConflict(UUID doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return leaveAbsenceRepository.existsByDoctorIdAndStatusAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
                doctorId,
                LeaveStatus.APPROVED,
                endDateTime,
                startDateTime
        );
    }

    private boolean hasSurgeryConflict(UUID doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate shiftDate = startDateTime.toLocalDate();
        if (!shiftDate.equals(endDateTime.toLocalDate())) {
            return true;
        }
        List<Shift> surgeryShifts = shiftRepository.findByDoctorIdAndShiftDateAndShiftTypeAndStatus(
                doctorId, shiftDate, com.project.model.ShiftType.SURGERY, ShiftStatus.ACTIVE
        );
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();
        return surgeryShifts.stream().anyMatch(shift ->
                !startTime.isAfter(shift.getEndTime()) && !endTime.isBefore(shift.getStartTime()));
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            throw new ApiException("INVALID_SLOT", "Invalid date format, expected yyyy-MM-dd", 400);
        }
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value);
        } catch (Exception ex) {
            throw new ApiException("INVALID_SLOT", "Invalid time format, expected HH:mm", 400);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (Exception ex) {
            throw new ApiException("INVALID_SLOT", "Invalid datetime format, expected yyyy-MM-dd HH:mm", 400);
        }
    }
}
