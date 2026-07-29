package com.habitflow.api.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.habitflow.api.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Verifikuje ID token koji Android dobije od Google Sign-In SDK-a.
 * GoogleIdTokenVerifier sam preuzima i osvezava Google-ove javne kljuceve
 * i proverava potpis, izdavaca (iss) i audience (aud == nas GOOGLE_CLIENT_ID).
 */
@Service
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${app.google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new UnauthorizedException("Nevažeći Google token");
            }
            return idToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new UnauthorizedException("Google token nije moguće verifikovati");
        }
    }
}
