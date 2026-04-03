package com.project.billing_service.client;

import com.project.billing_service.dto.ClaimRequestDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClaimClient {
    private final RestTemplate restTemplate;

    @Value("${claim.api.url:https://httpbin.org/status/200}")
    private String claimApiUrl;

    public ClaimClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "claimProvider", fallbackMethod = "submitClaimFallback")
    public boolean submitClaim(ClaimRequestDto requestDto) {
        ResponseEntity<Void> response = restTemplate.postForEntity(claimApiUrl, requestDto, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    private boolean submitClaimFallback(ClaimRequestDto requestDto, Throwable throwable) {
        return false;
    }
}
