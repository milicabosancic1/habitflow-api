package com.habitflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @NotBlank(message = "Ime je obavezno")
    @Size(max = 255, message = "Ime je predugačko")
    private String displayName;

    @Size(max = 500, message = "Izjava o identitetu je predugačka")
    private String identityStatement;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getIdentityStatement() { return identityStatement; }
    public void setIdentityStatement(String identityStatement) { this.identityStatement = identityStatement; }
}
