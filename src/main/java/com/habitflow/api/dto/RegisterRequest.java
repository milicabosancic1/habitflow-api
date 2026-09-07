package com.habitflow.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Email @NotBlank @Size(max = 255, message = "Email je predugačak")
    private String email;

    // BCrypt tiho secce sve preko 72 bajta (poznato ogranicenje algoritma) — bez ovog
    // maksimuma korisnik bi mislio da mu cela lozinka vazi, a proveravalo bi se samo prvih 72.
    @NotBlank @Size(min = 6, max = 72, message = "Lozinka mora imati između 6 i 72 karaktera")
    private String password;

    @NotBlank @Size(max = 255, message = "Ime je predugačko")
    private String displayName;

    @Size(max = 500, message = "Izjava o identitetu je predugačka")
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
