package com.project.service;

import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.DoctorNotFoundException;
import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.PatientLimitException;
import com.project.helper.DoctorMapper;
import com.project.helper.DoctorValidator;
import com.project.model.Doctor;
import com.project.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;
    private final DoctorValidator doctorValidator;


    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper, DoctorValidator doctorValidator) {
        this.doctorRepository = doctorRepository;
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
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    
    // MAINTAINING
    public void increasePatientNumber(UUID id) throws PatientLimitException {
        Optional<Doctor> byId = doctorRepository.findById(id);
        if (byId.get().getPatientCount() == 5) throw new PatientLimitException("Patient limit is full");
        byId.get().setPatientCount(byId.get().getPatientCount() + 1);
    }
}
