package com.habitflow.api.dto;

public class AuthResponse {
    private String userId;
    private String token;
    private String refreshToken;
    private String displayName;

    public AuthResponse() {}
    public AuthResponse(String userId, String token, String refreshToken, String displayName) {
        this.userId = userId;
        this.token = token;
        this.refreshToken = refreshToken;
        this.displayName = displayName;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
