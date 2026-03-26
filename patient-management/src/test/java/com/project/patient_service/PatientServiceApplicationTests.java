package com.project.patient_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.project.patient_service.security.JwtAuthFilter;

@SpringBootTest
class PatientServiceApplicationTests {

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

	@Test
	void contextLoads() {
	}

}
