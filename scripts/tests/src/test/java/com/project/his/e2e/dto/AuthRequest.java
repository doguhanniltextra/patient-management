package com.project.his.e2e.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private String registerDate; // Use String to avoid Jackson JSR310 issues in test module
}
