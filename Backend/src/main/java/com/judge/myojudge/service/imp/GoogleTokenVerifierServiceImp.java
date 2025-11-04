package com.judge.myojudge.service.imp;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.judge.myojudge.service.GoogleTokenVerifierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifierServiceImp implements GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierServiceImp(@Value("${google.client-id}") String googleClientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Override
    public GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return (idToken != null) ? idToken.getPayload() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
