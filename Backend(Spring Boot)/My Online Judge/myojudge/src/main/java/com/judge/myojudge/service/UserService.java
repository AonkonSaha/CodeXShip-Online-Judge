//package com.judge.myojudge.service;
//
//import com.judge.myojudge.repo.UserRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepo userRepository;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private VerificationTokenRepo tokenRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public void registerUser(User user) {
//        // Check if the user already exists
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            throw new RuntimeException("User with this email already exists.");
//        }
//
//        // Encode the password
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setEnabled(false); // Disable the user until email verification
//        userRepository.save(user);
//
//        // Generate a verification token
//        String token = UUID.randomUUID().toString();
//        VerificationToken verificationToken = new VerificationToken(token, user);
//        tokenRepository.save(verificationToken);
//
//        // Send verification email
//        sendVerificationEmail(user.getEmail(), token);
//    }
//
//    private void sendVerificationEmail(String email, String token) {
//        String verificationUrl = "http://localhost:3716/verify-email?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Email Verification");
//        message.setText("Click the link to verify your email: " + verificationUrl);
//        message.setFrom("scm@demomailtrap.com");
//        System.out.println("I am Email");
//        mailSender.send(message);
//
//    }
//
//    public void verifyToken(String token) {
//        VerificationToken verificationToken = tokenRepository.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token."));
//
//        // Activate the user
//        User user = verificationToken.getUser();
//        user.setEnabled(true);
//        userRepository.save(user);
//
//        // Delete the token after successful verification
//        tokenRepository.delete(verificationToken);
//    }
//}
//





//////////*********submit Code**************************************************************/

//
//
//
//
//
//package com.judge.myojudge.execution_validations_code.ev_service;
//
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.util.stream.Collectors;
//
//@Service
//public class CodeRunner {
//
//    public String runCode(String language, String code, String input) {
//        if ("python".equalsIgnoreCase(language)) {
//            return executePythonCode(code, input);
//        }
//        return "Error: Unsupported language - " + language;
//    }
//
//    private String executePythonCode(String code, String input) {
//        ProcessBuilder builder = new ProcessBuilder("python3", "-c", code);
//        builder.redirectErrorStream(true);
//
//        try {
//            Process process = builder.start();
//
//            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
//                writer.write(input);
//                writer.newLine();
//                writer.flush();
//            }
//
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                return reader.lines().collect(Collectors.joining("\n"));
//            }
//        } catch (IOException e) {
//            return "Execution Error: " + e.getMessage();
//        }
//    }
//}


