package com.project.Repository;


import com.project.model.Doctor;
import com.project.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static com.project.model.Specialization.Dermatologist;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class DoctorServiceRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;


    @Test
    public void DoctorService_existsByEmail_ReturnsTrue(){
// Arrange - Tüm @NotNull alanları doldurarak nesneyi hazırla
        String mockEmail = "doctor.mock@hospital.com";

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist); // Enum değerine göre güncelle
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);


        doctorRepository.save(doctor);

        // Act
        Boolean bool = doctorRepository.existsByEmail(mockEmail);

        // Assert
        assertThat(bool).isTrue();
    }

    @Test
    public void DoctorService_existsByEmail_ReturnsFalse(){
// Arrange - Tüm @NotNull alanları doldurarak nesneyi hazırla
        String mockEmail = "doctor.mock@hospital.com";

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist); // Enum değerine göre güncelle
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);


        // doctorRepository.save(doctor);

        // Act
        Boolean bool = doctorRepository.existsByEmail(mockEmail);

        // Assert
        assertThat(bool).isFalse();
    }

    @Test
    public void DoctorRepository_ExistsByEmailAndIdNot_ReturnsTrue() {
        // Arrange
        String mockEmail = "doctor.mock@hospital.com";
        UUID secondId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist);
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);

        Doctor savedDoctor = doctorRepository.save(doctor);
        UUID actualDoctorId = savedDoctor.getId();

        // Act
        Boolean bool = doctorRepository.existsByEmailAndIdNot(mockEmail, secondId);

        // Assert
        assertThat(bool).isTrue();
        assertThat(actualDoctorId).isNotEqualTo(secondId);
    }

    @Test
    public void DoctorService_findByEmail_ReturnsEmail() {
        String mockEmail = "doctor.mock@hospital.com";

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist);
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);

        Doctor newDoctor = doctorRepository.save(doctor);

        String email = doctorRepository.findByEmail(newDoctor.getEmail());

        assertThat(email).isEqualTo(mockEmail);
    }
}
