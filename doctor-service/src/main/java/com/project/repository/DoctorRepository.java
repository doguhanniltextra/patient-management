package com.project.repository;

import com.project.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorRepository  extends JpaRepository<Doctor, UUID> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    @Query("SELECT d.email FROM Doctor d WHERE d.email = :email")
    String findByEmail(String email);
}
