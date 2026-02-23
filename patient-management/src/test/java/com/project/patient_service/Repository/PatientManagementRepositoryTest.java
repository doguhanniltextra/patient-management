package com.project.patient_service.Repository;


import com.project.patient_service.model.Patient;
import com.project.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class PatientManagementRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;


    @Test
    public void PatientRepository_ExistsByEmail_ReturnsTrue() {
        // Arrange:
        String testEmail = "test_email@gmail.com";
        Patient patient = new Patient();

        patient.setName("Test Patient");
        patient.setEmail(testEmail);
        patient.setAddress("Istanbul, Turkey");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.now());

        patientRepository.save(patient);

        // Act
        boolean exists = patientRepository.existsByEmail(testEmail);

        // Assert
        // import static org.assertj.core.api.Assertions.assertThat;
        assertThat(exists).isTrue();
    }

    @Test
    public void PatientRepository_ExistsByEmailAndIdNot_ReturnsFalse() {
        // Arrange:
        String testEmail = "test_email@gmail.com";
        Patient patient = new Patient();

        patient.setName("Test Patient");
        patient.setEmail(testEmail);
        patient.setAddress("Istanbul, Turkey");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.now());

        patientRepository.save(patient);


         Patient newPatient = patientRepository.findByEmail(patient.getEmail());

         boolean bool = patientRepository.existsByEmailAndIdNot(newPatient.getEmail(), newPatient.getId());

         assertThat(bool).isFalse();
    }

    @Test
    public void PatientRepository_FindByEmail_ReturnsPatient() {
        // Arrange
        String testEmail = "test@gmail.com";
        Patient patient = new Patient();

        patient.setName("Test Patient");
        patient.setEmail(testEmail);
        patient.setAddress("Istanbul, Turkey");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.now());

        // Act
        patientRepository.save(patient);
        Patient newPatient = patientRepository.findByEmail(patient.getEmail());

        // Assert
        assertThat(newPatient).isInstanceOf(Patient.class);
        assertThat(newPatient).isNotNull();
        assertThat(newPatient.getEmail()).isEqualTo(testEmail);
    }

}
