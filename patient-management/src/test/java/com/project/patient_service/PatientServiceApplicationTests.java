package com.project.patient_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.project.patient_service.security.JwtAuthFilter;

@SpringBootTest
@org.springframework.test.context.TestPropertySource(properties = "grpc.server.port=-1")
class PatientServiceApplicationTests {

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

	@Test
	void contextLoads() {
	}

}
