package com.project.auth_service.dto;

public class LoginResponseDto {
    private String message;
    private String token;
    public LoginResponseDto(String loginSuccessful, String token) {
        this.message = loginSuccessful;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
