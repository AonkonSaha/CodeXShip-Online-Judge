package com.judge.myojudge.controller;

import com.judge.myojudge.model.dto.*;
import com.judge.myojudge.model.mapper.UserMapper;
import com.judge.myojudge.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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

    @PostMapping("/v1/register")
    public ResponseEntity<RegisterDTO> register(@RequestBody RegisterDTO registerDTO) {
        System.out.println(registerDTO);
        return ResponseEntity.ok(userMapper.toUserRegisterDTO(
                authService.saveUser(userMapper.toUser(registerDTO))));
    }
    @PostMapping("/v1/login")
    public  ResponseEntity<Map<String,String>> login(@RequestBody LoginDTO userDTO) {
        System.out.println(userDTO);
        return ResponseEntity.ok(Map.of("token",authService.login(userDTO)));
    }
    @PostMapping("/v1/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        String token = request.getHeader("token").substring(7);
        authService.logout(token);
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/v1/update/user")
    public ResponseEntity<UpdateUserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        String mobile= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUserDetails(mobile,updateUserDTO);
        return ResponseEntity.ok(updateUserDTO);
    }

    @PostMapping("/v1/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordDTO passwordDTO) throws BadRequestException {
        if(!Objects.equals(passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword())){
            throw new BadRequestException("Passwords don't match");
        }
        String mobile= SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateUserPassword(mobile,passwordDTO);
        return ResponseEntity.ok("Password Changed Successfully");
    }

    @GetMapping("/v1/get/user/details")
    public ResponseEntity<UserDTO> getUserDetails(){
     String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Controller mobile: "+mobile);
     return ResponseEntity.ok(userMapper.toUpdateUserDTO(authService.fetchUserDetails(mobile)));
    }
    


}
