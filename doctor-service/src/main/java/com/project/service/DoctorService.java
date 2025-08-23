package com.project.service;

import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.PatientLimitException;
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

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public UpdateDoctorServiceResponseDto updateDoctor(UUID id, UpdateDoctorServiceRequestDto updateDoctorServiceRequestDto) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);

        if (optionalDoctor.isPresent()) {
            Doctor existingDoctor = optionalDoctor.get();
            existingDoctor.setName(updateDoctorServiceRequestDto.getName());
            existingDoctor.setSpecialization(updateDoctorServiceRequestDto.getSpecialization());
            existingDoctor.setEmail(updateDoctorServiceRequestDto.getEmail());
            existingDoctor.setHospitalName(updateDoctorServiceRequestDto.getHospitalName());
            existingDoctor.setLicenseNumber(updateDoctorServiceRequestDto.getLicenseNumber());
            doctorRepository.save(existingDoctor);

            UpdateDoctorServiceResponseDto updateDoctorServiceResponseDto = getUpdateDoctorServiceResponseDto(existingDoctor);
            return updateDoctorServiceResponseDto;
        } else {
            throw new EntityNotFoundException("Doctor with id " + id + " not found.");
        }
    }

    private static UpdateDoctorServiceResponseDto getUpdateDoctorServiceResponseDto(Doctor existingDoctor) {
        UpdateDoctorServiceResponseDto updateDoctorServiceResponseDto = new UpdateDoctorServiceResponseDto();
        updateDoctorServiceResponseDto.setId(existingDoctor.getId());
        updateDoctorServiceResponseDto.setName(existingDoctor.getName());
        updateDoctorServiceResponseDto.setEmail(existingDoctor.getEmail());
        updateDoctorServiceResponseDto.setNumber(existingDoctor.getNumber());
        updateDoctorServiceResponseDto.setSpecialization(existingDoctor.getSpecialization());
        updateDoctorServiceResponseDto.setYearsOfExperience(existingDoctor.getYearsOfExperience());
        updateDoctorServiceResponseDto.setHospitalName(existingDoctor.getHospitalName());
        updateDoctorServiceResponseDto.setDepartment(existingDoctor.getDepartment());
        updateDoctorServiceResponseDto.setLicenseNumber(existingDoctor.getLicenseNumber());
        updateDoctorServiceResponseDto.setAvailable(existingDoctor.isAvailable());
        updateDoctorServiceResponseDto.setPatientCount(existingDoctor.getPatientCount());
        updateDoctorServiceResponseDto.setGetMaximumPatient(existingDoctor.isGetMaximumPatient());
        return updateDoctorServiceResponseDto;
    }

    @Transactional
    public CreateDoctorServiceResponseDto createDoctor(@Valid CreateDoctorServiceRequestDto createDoctorServiceRequestDto) throws  EmailIsNotUniqueException {

        boolean byEmail = doctorRepository.existsByEmail(createDoctorServiceRequestDto.getEmail());
        if (byEmail) {
            throw new EmailIsNotUniqueException("Email is not unique.");
        }

        Doctor doctor = getDoctor(createDoctorServiceRequestDto);

        Doctor result = doctorRepository.save(doctor);
        CreateDoctorServiceResponseDto createDoctorServiceResponseDto = new CreateDoctorServiceResponseDto();
        createDoctorServiceResponseDto.setName(result.getName());
        createDoctorServiceResponseDto.setEmail(result.getEmail());
        createDoctorServiceResponseDto.setId(result.getId());
        createDoctorServiceResponseDto.setNumber(result.getNumber());
        createDoctorServiceResponseDto.setHospitalName(result.getHospitalName());

        return createDoctorServiceResponseDto;
    }

    private static Doctor getDoctor(CreateDoctorServiceRequestDto createDoctorServiceRequestDto) {
        Doctor doctor = new Doctor();
        doctor.setId(createDoctorServiceRequestDto.getId());
        doctor.setName(createDoctorServiceRequestDto.getName());
        doctor.setEmail(createDoctorServiceRequestDto.getEmail());
        doctor.setNumber(createDoctorServiceRequestDto.getNumber());
        doctor.setSpecialization(createDoctorServiceRequestDto.getSpecialization());
        doctor.setYearsOfExperience(createDoctorServiceRequestDto.getYearsOfExperience());
        doctor.setHospitalName(createDoctorServiceRequestDto.getHospitalName());
        doctor.setDepartment(createDoctorServiceRequestDto.getDepartment());
        doctor.setLicenseNumber(createDoctorServiceRequestDto.getLicenseNumber());
        doctor.setAvailable(createDoctorServiceRequestDto.isAvailable());
        doctor.setPatientCount(createDoctorServiceRequestDto.getPatientCount());
        return doctor;
    }


    public void deleteDoctor(UUID id) {
        doctorRepository.deleteById(id);
    }

    public Optional<Doctor> findDoctorById(UUID id) {
        Optional<Doctor> byId = doctorRepository.findById(id);
        return byId;
    }

    // MAINTAINING
    public void increasePatientNumber(UUID id) throws PatientLimitException {
        Optional<Doctor> byId = doctorRepository.findById(id);
        if (byId.get().getPatientCount() == 5) throw new PatientLimitException("Patient limit is full");
        byId.get().setPatientCount(byId.get().getPatientCount() + 1);
    }
}
