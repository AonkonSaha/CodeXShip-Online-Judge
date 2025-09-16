package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.validation.UserValidation;
import com.judge.myojudge.validation.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final ValidationService validationService;

    @PostMapping("/v1/register")
    public ResponseEntity<ApiResponse<RegisterDTO>> register(@RequestBody RegisterDTO registerDTO) {
        validationService.validateUserDetails(registerDTO);
        ApiResponse<RegisterDTO> apiResponse= ApiResponse.<RegisterDTO>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("Registration Success..!")
                .data(userMapper.toUserRegisterDTO
                        (authService.saveUser(userMapper.toUser(registerDTO))))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }
    @PostMapping("/v1/login")
    public  ResponseEntity<ApiResponse<Map<String,String>>> login(@RequestBody LoginDTO loginDTO) {
        validationService.validateLoginDetails(loginDTO);
        ApiResponse<Map<String,String>> apiResponse=ApiResponse.<Map<String,String>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Login Success..!")
                .data(Map.of("token", authService.login(loginDTO)))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @PostMapping("/v1/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){
        String token = request.getHeader("token").substring(7);
        authService.logout(token);
        return  ResponseEntity.noContent().build() ;
    }

    @PostMapping("/v1/update")
    public ResponseEntity<Void> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        String mobile= SecurityContextHolder.getContext().getAuthentication().getName();
        validationService.validateUserDetails(updateUserDTO);
        authService.updateUserDetails(mobile,updateUserDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/update/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordDTO passwordDTO) throws BadRequestException {
        String mobile= SecurityContextHolder.getContext().getAuthentication().getName();
        validationService.validateUserPassword(passwordDTO);
        authService.updateUserPassword(mobile,passwordDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserDetails(){
     String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
     ApiResponse<UserDTO> apiResponse=ApiResponse.<UserDTO>builder()
             .success(true)
             .statusCode(HttpStatus.OK.value())
             .message("User Data Fetched Successfully..!")
             .data(userMapper.toUpdateUserDTO(authService.fetchUserDetails(mobile)))
             .build();
     return ResponseEntity.ok(apiResponse);
    }

}
