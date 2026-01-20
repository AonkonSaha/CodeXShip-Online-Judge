package com.judge.myojudge.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.judge.myojudge.model.dto.LoginRequest;
import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserRegisterResponse;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.GoogleTokenVerifierService;
import com.judge.myojudge.validation.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final ValidationService validationService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        ApiResponse<UserRegisterResponse> apiResponse= ApiResponse.<UserRegisterResponse>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("Registration Success..!")
                .data(userMapper.toUserRegisterResponse
                        (authService.saveUser(userMapper.toUser(userRegisterRequest))))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    @PostMapping("/login")
    public  ResponseEntity<ApiResponse<Map<String,String>>> login(@RequestBody @Valid LoginRequest loginRequest) {
        ApiResponse<Map<String,String>> apiResponse=ApiResponse.<Map<String,String>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Login Success..!")
                .data(Map.of("token", authService.login(loginRequest)))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse<Map<String, String>>> googleLogin(@RequestBody Map<String, String> body) {
        String email = null;
        String name = null;
        String picture = null;

        try {
            String idToken = body.get("credential");
            if (idToken == null || idToken.isEmpty()) {
                throw new BadCredentialsException("Missing Google credential token");
            }
            GoogleIdToken.Payload payload = googleTokenVerifierService.verifyGoogleIdToken(idToken);
            if (payload == null) {
                throw new BadCredentialsException("Invalid Google IDToken");
            }
            email = payload.getEmail();
            name = (String) payload.get("name");
            picture = (String) payload.get("picture");
            String appJwt = authService.loginByGoogle(email, name, picture);
            ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Login Success..!")
                    .data(Map.of("token", appJwt))
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BadCredentialsException("Login Failed..!", e);
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token= request.getHeader("Authorization").substring(7);
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.logout(mobileOrEmail,token);
        return  ResponseEntity.noContent().build() ;
    }

}
