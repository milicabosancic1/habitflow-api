package com.habitflow.api.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    @NotBlank(message = "refreshToken je obavezan")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
