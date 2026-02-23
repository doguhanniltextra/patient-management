package com.project.patient_service.Kafka;


import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.helper.UserValidator;
import com.project.patient_service.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    void sendEvent_WhenSuccess_ShouldSendJson() {
        // Arrange
        KafkaPatientRequestDto dto = new KafkaPatientRequestDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Kafka Test");

        Map<String, Object> mockMap = new HashMap<>();
        mockMap.put("name", "Kafka Test");

        when(userValidator.getStringObjectMap(dto)).thenReturn(mockMap);

        // Act
        kafkaProducer.sendEvent(dto);

        // Assert
        verify(userValidator, times(1)).sendKafkaEvent(anyString(), eq(kafkaTemplate));
    }

    @Test
    void sendEvent_WhenExceptionOccurs_ShouldCatchAndLog() {
        KafkaPatientRequestDto dto = new KafkaPatientRequestDto();

        when(userValidator.getStringObjectMap(any())).thenThrow(new RuntimeException("Kafka Error"));
        kafkaProducer.sendEvent(dto);

        verify(userValidator, never()).sendKafkaEvent(anyString(), any());
    }
}