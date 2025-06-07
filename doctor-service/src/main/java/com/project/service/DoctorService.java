package com.project.service;

import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.IdIsValidException.IdIsValidException;
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

    public Doctor updateDoctor(UUID id, Doctor doctor) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);

        if (optionalDoctor.isPresent()) {
            Doctor existingDoctor = optionalDoctor.get();
            existingDoctor.setName(doctor.getName());
            existingDoctor.setSpecialization(doctor.getSpecialization());
            existingDoctor.setEmail(doctor.getEmail());
            existingDoctor.setHospitalName(doctor.getHospitalName());
            existingDoctor.setLicenseNumber(doctor.getLicenseNumber());

            return doctorRepository.save(existingDoctor);
        } else {
            throw new EntityNotFoundException("Doctor with id " + id + " not found.");
        }
    }

    @Transactional
    public Doctor createDoctor(@Valid Doctor doctor) throws IdIsValidException, EmailIsNotUniqueException {

        boolean byEmail = doctorRepository.existsByEmail(doctor.getEmail());
        if (byEmail) {
            throw new EmailIsNotUniqueException("Email is not unique.");
        }

        return doctorRepository.save(doctor);
    }


    public void deleteDoctor(UUID id) {
        doctorRepository.deleteById(id);
    }

    public Optional<Doctor> findDoctorById(UUID id) {
        Optional<Doctor> byId = doctorRepository.findById(id);
        return byId;
    }

    public void increasePatientNumber(UUID id) throws PatientLimitException {
        Optional<Doctor> byId = doctorRepository.findById(id);
        if (byId.get().getPatientCount() == 5) throw new PatientLimitException("Patient limit is full");
        byId.get().setPatientCount(byId.get().getPatientCount() + 1);
    }
}
