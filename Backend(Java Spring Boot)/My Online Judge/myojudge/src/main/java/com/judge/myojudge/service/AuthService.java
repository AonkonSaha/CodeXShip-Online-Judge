package com.judge.myojudge.service;

import com.fasterxml.jackson.databind.annotation.EnumNaming;
import com.judge.myojudge.dto.UserLogin;
import com.judge.myojudge.dto.UserRegister;
import com.judge.myojudge.enums.Role;
import com.judge.myojudge.jwt.JwtUtil;
import com.judge.myojudge.model.User;
import com.judge.myojudge.repo.UserRepo;
import jakarta.persistence.Enumerated;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public String register(UserRegister userRegister) {
        if (userRepository.findByUsername(userRegister.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        if (userRepository.findByEmail(userRegister.getEmail()).isPresent()) {
            throw new RuntimeException("email already exists");
        }
        User user = new User(null, userRegister.getUsername(),userRegister.getEmail(), passwordEncoder.encode(userRegister.getPassword()), Role.USER,false);
//        User user = new User(null, userRegister.getUsername(),userRegister.getEmail(), passwordEncoder.encode(userRegister.getPassword()), Role.ADMIN,false);
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String login(UserLogin userLogin) {


        User user = userRepository.findByUsername(userLogin.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        user.setActivityStatus(true);
        userRepository.save(user);
        return jwtUtil.generateToken(user.getUsername(),user.getActivityStatus(),user.getRole().toString());
    }
    public String logout(HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Invalid token";
        }
        String token = authHeader.substring(7);
        String username=jwtUtil.extractUsername(token);
        User user=userRepository.findByUsername(username).orElseThrow();
        user.setActivityStatus(false);
        userRepository.save(user);
        return "Logout Successfully!";
    }
}