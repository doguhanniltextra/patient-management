package com.project.helper;

import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.EmailIsNotUniqueException;
import com.project.repository.DoctorRepository;
import org.springframework.stereotype.Component;

@Component
public class DoctorValidator {

    public void checkEmailIsUniqueOrNotForCreate(CreateDoctorServiceRequestDto dto, DoctorRepository doctorRepository) throws EmailIsNotUniqueException {
        if (doctorRepository.existsByEmail(dto.getEmail())) {
            throw new EmailIsNotUniqueException("Email is not unique.");
        }
    }
}
