package com.habitflow.api.dto;

public class AuthResponse {
    private String userId;
    private String token;
    private String displayName;

    public AuthResponse() {}
    public AuthResponse(String userId, String token, String displayName) {
        this.userId = userId;
        this.token = token;
        this.displayName = displayName;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
