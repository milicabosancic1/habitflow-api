package com.habitflow.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 6, message = "Lozinka mora imati bar 6 karaktera")
    private String password;

    @NotBlank
    private String displayName;

    private String identityStatement;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getIdentityStatement() { return identityStatement; }
    public void setIdentityStatement(String identityStatement) { this.identityStatement = identityStatement; }
}
