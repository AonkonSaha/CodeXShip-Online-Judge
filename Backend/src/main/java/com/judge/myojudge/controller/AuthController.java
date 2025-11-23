package com.judge.myojudge.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.judge.myojudge.exception.ImageSizeLimitExceededException;
import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.service.AuthService;
import com.judge.myojudge.service.GoogleTokenVerifierService;
import com.judge.myojudge.service.RankService;
import com.judge.myojudge.validation.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final ValidationService validationService;
    private final RankService rankService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    @PostMapping("/v1/register")
    public ResponseEntity<ApiResponse<RegisterUserDTO>> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        ApiResponse<RegisterUserDTO> apiResponse= ApiResponse.<RegisterUserDTO>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("Registration Success..!")
                .data(userMapper.toUserRegisterDTO
                        (authService.saveUser(userMapper.toUser(registerUserDTO))))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }
    @PostMapping("/v1/login")
    public  ResponseEntity<ApiResponse<Map<String,String>>> login(@RequestBody @Valid LoginDTO loginDTO) {
        ApiResponse<Map<String,String>> apiResponse=ApiResponse.<Map<String,String>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Login Success..!")
                .data(Map.of("token", authService.login(loginDTO)))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @PostMapping("/v2/login/google")
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
            throw new BadCredentialsException("Login Failed..!");
        }
    }

    @PostMapping("/v1/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token= request.getHeader("Authorization").substring(7);
        String mobileOrEmail=SecurityContextHolder.getContext().getAuthentication().getName();
        authService.logout(mobileOrEmail,token);
        return  ResponseEntity.noContent().build() ;
    }

    @PutMapping("/v1/update")
    @PreAuthorize("hasAnyRole('ADMIN','NORMAL_USER')")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUserDetails(mobileOrEmail,updateUserDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v2/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> updateUserByAdmin(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        authService.updateUserDetailsByAdmin(updateUserDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/update/password")
    @PreAuthorize("hasAnyRole('ADMIN','NORMAL_USER')")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordDTO passwordDTO) throws BadRequestException {
        String mobileOrEmail= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUserPassword(mobileOrEmail,passwordDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/update/profile-pic")
    @PreAuthorize("hasAnyRole('ADMIN','NORMAL_USER')")
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

    @GetMapping("/v1/profile")
    @PreAuthorize("hasAnyRole('ADMIN','NORMAL_USER')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserDetails(){
     String mobileOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
     ApiResponse<UserDTO> apiResponse=ApiResponse.<UserDTO>builder()
             .success(true)
             .statusCode(HttpStatus.OK.value())
             .message("User Data Fetched Successfully..!")
             .data(userMapper.toUpdateUserDTO(authService.fetchUserDetails(mobileOrEmail)))
             .build();
     return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/v1/profile/{username}/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserDetailsByUsername(@PathVariable String username,@PathVariable Long userId){
        ApiResponse<UserDTO> apiResponse=ApiResponse.<UserDTO>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("User Data Fetched Successfully..!")
                .data(userMapper.toUpdateUserDTO(authService.fetchUserDetailsByUsername(username,userId)))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/v1/get/users")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<User> users = authService.getUsers(search,pageable);

        List<UserDTO> userDTOS= userMapper.toUserDTO(users.getContent());
        return ResponseEntity.ok(ApiResponse.<Page<UserDTO>>builder()
                .success(true)
                .statusCode(200)
                .message("Ranking Fetched Successfully")
                .data(new PageImpl<>(userDTOS, pageable,users.getTotalElements()))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/v1/delete/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email){
        authService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

}
