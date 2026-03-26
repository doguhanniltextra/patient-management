package com.project.security;

import com.project.repository.DoctorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("securityService")
public class SecurityOwnershipService {

    private final DoctorRepository doctorRepository;

    public SecurityOwnershipService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public boolean isDoctorOwner(Authentication authentication, UUID doctorId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String name = authentication.getName();
        return doctorRepository.findById(doctorId)
                .map(doctor -> doctor.getName() != null && doctor.getName().equals(name))
                .orElse(false);
    }
}
