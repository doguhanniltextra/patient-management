package com.project.helper;

import com.project.dto.UpdateDoctorControllerResponseDto;
import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.request.CreateDoctorControllerRequestDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.exception.EmailIsNotUniqueException;
import com.project.model.Doctor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoctorMapper {


    public  Doctor toEntity(CreateDoctorServiceRequestDto createDoctorServiceRequestDto) {
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


    public  CreateDoctorServiceResponseDto toCreateDoctorServiceResponseDto(Doctor result) {
        CreateDoctorServiceResponseDto createDoctorServiceResponseDto = new CreateDoctorServiceResponseDto();
        createDoctorServiceResponseDto.setName(result.getName());
        createDoctorServiceResponseDto.setEmail(result.getEmail());
        createDoctorServiceResponseDto.setId(result.getId());
        createDoctorServiceResponseDto.setNumber(result.getNumber());
        createDoctorServiceResponseDto.setHospitalName(result.getHospitalName());
        return createDoctorServiceResponseDto;
    }

    public  UpdateDoctorServiceResponseDto getUpdateDoctorServiceResponseDto(Doctor existingDoctor) {
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

    public  Doctor getDoctorRequestDto(UpdateDoctorServiceRequestDto updateDoctorServiceRequestDto, Optional<Doctor> optionalDoctor) {
        Doctor existingDoctor = optionalDoctor.get();
        existingDoctor.setName(updateDoctorServiceRequestDto.getName());
        existingDoctor.setSpecialization(updateDoctorServiceRequestDto.getSpecialization());
        existingDoctor.setEmail(updateDoctorServiceRequestDto.getEmail());
        existingDoctor.setHospitalName(updateDoctorServiceRequestDto.getHospitalName());
        existingDoctor.setLicenseNumber(updateDoctorServiceRequestDto.getLicenseNumber());
        return existingDoctor;
    }

    public static UpdateDoctorControllerResponseDto getDoctorControllerResponseDto(UpdateDoctorServiceResponseDto updateDoctorServiceResponseDto) {
        UpdateDoctorControllerResponseDto updateDoctorControllerResponseDto = new UpdateDoctorControllerResponseDto();
        updateDoctorControllerResponseDto.setId(updateDoctorServiceResponseDto.getId());
        updateDoctorControllerResponseDto.setName(updateDoctorServiceResponseDto.getName());
        updateDoctorControllerResponseDto.setNumber(updateDoctorServiceResponseDto.getNumber());
        updateDoctorControllerResponseDto.setEmail(updateDoctorServiceResponseDto.getEmail());
        updateDoctorControllerResponseDto.setSpecialization(updateDoctorServiceResponseDto.getSpecialization());
        updateDoctorControllerResponseDto.setYearsOfExperience(updateDoctorServiceResponseDto.getYearsOfExperience());
        return updateDoctorControllerResponseDto;
    }

    public static CreateDoctorServiceRequestDto getCreateDoctorServiceRequestDto(CreateDoctorControllerRequestDto createDoctorControllerRequestDto) {
        CreateDoctorServiceRequestDto createDoctorServiceRequestDto = new CreateDoctorServiceRequestDto();
        createDoctorServiceRequestDto.setId(createDoctorControllerRequestDto.getId());
        createDoctorServiceRequestDto.setName(createDoctorControllerRequestDto.getName());
        createDoctorServiceRequestDto.setEmail(createDoctorControllerRequestDto.getEmail());
        createDoctorServiceRequestDto.setNumber(createDoctorControllerRequestDto.getNumber());
        createDoctorServiceRequestDto.setNumber(createDoctorControllerRequestDto.getNumber());
        createDoctorServiceRequestDto.setSpecialization(createDoctorControllerRequestDto.getSpecialization());
        createDoctorServiceRequestDto.setYearsOfExperience(createDoctorControllerRequestDto.getYearsOfExperience());
        createDoctorServiceRequestDto.setHospitalName(createDoctorControllerRequestDto.getHospitalName());
        createDoctorServiceRequestDto.setDepartment(createDoctorControllerRequestDto.getDepartment());
        createDoctorServiceRequestDto.setLicenseNumber(createDoctorControllerRequestDto.getLicenseNumber());
        createDoctorServiceRequestDto.setAvailable(createDoctorControllerRequestDto.isAvailable());
        createDoctorServiceRequestDto.setPatientCount(createDoctorControllerRequestDto.getPatientCount());
        return createDoctorServiceRequestDto;
    }
}
