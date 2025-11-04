package com.judge.myojudge.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public interface GoogleTokenVerifierService {

    GoogleIdToken.Payload verifyGoogleIdToken(String idToken);
}
