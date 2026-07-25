package com.habitflow.api.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {
    @NotBlank(message = "Ime je obavezno")
    private String displayName;

    private String identityStatement;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getIdentityStatement() { return identityStatement; }
    public void setIdentityStatement(String identityStatement) { this.identityStatement = identityStatement; }
}
