package com.judge.myojudge.controller;

import com.judge.myojudge.exception.ImageSizeLimitExceededException;
import com.judge.myojudge.model.dto.PasswordRequest;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.UserUpdateRequest;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.GoogleTokenVerifierService;
import com.judge.myojudge.service.RankService;
import com.judge.myojudge.validation.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final ValidationService validationService;
    private final RankService rankService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(){
        String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<UserResponse> apiResponse=ApiResponse.<UserResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("User Data Fetched Successfully..!")
                .data(userMapper.toUserResponse(authService.getUserByMobileOrEmail(mobileOrEmail)))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<Void> updateProfile(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUserDetails(mobileOrEmail, userUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordRequest passwordRequest) throws BadRequestException {
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUserPassword(mobileOrEmail, passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<ApiResponse<String>> updateProfilePic(@RequestParam("file") MultipartFile file) throws Exception {
        if(file==null || file.isEmpty()){
            throw new FileNotFoundException("File Not Found");
        }
        if(file.getSize()>5*1024*1024){
            throw new ImageSizeLimitExceededException("Image size doesn't exceed in 5MB");
        }
        ApiResponse<String> apiResponse=ApiResponse.<String>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Image Upload Successfully!")
                .data(authService.updateProfileImage(file))
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }



    @GetMapping("/me/{username}/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROBLEM_EDITOR',NORMAL_USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetailsByUsername(
            @PathVariable String username,
            @PathVariable Long userId){
        ApiResponse<UserResponse> apiResponse=ApiResponse.<UserResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("User Data Fetched Successfully..!")
                .data(userMapper.toUserResponse(authService.getUserById(userId)))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}
