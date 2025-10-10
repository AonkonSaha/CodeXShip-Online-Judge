package com.judge.myojudge.service.imp;

import com.judge.myojudge.exception.UserNotFoundException;
import com.judge.myojudge.jwt.JwtUtil;
import com.judge.myojudge.model.dto.LoginDTO;
import com.judge.myojudge.model.dto.PasswordDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;
import com.judge.myojudge.model.entity.BlockedToken;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.repository.BlockedTokenRepo;
import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlockedTokenRepo blockedTokenRepo;


    @Override
    public User register(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public String login(LoginDTO loginDTO) {
        User user = userRepository.findByMobileNumber(loginDTO.getMobile()).get();
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
           throw new BadCredentialsException("Incorrect Password");
        }
        user.setActivityStatus(true);
        userRepository.save(user);
        return jwtUtil.generateToken(user,user.getMobileNumber(),user.getActivityStatus());
    }

    @Override
    public void logout(String token)
    {
        String username=jwtUtil.extractUsername(token);
        Optional<User> user=userRepository.findByUsername(username);
        if(user.isEmpty()){
          throw new UserNotFoundException("User not found");
        }
        user.get().setActivityStatus(false);
        blockedTokenRepo.save(new BlockedToken(token));
        userRepository.save(user.get());
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserDetails(String mobile,UpdateUserDTO updateUserDTO) {
        User user = userRepository.findByMobileNumber(mobile).orElseThrow(()->new UserNotFoundException("User not found"));
        user.setMobileNumber(updateUserDTO.getMobileNumber());
        user.setEmail(updateUserDTO.getEmail());
        user.setCountry(updateUserDTO.getCountry());
        user.setCity(updateUserDTO.getCity());
        user.setPostalCode(updateUserDTO.getPostalCode());
        user.setGender(updateUserDTO.getGender());
        return userRepository.save(user);

    }

    @Override
    public void updateUserPassword(String mobile,PasswordDTO passwordDTO) {
        User user = userRepository.findByMobileNumber(mobile).orElseThrow(()->new UserNotFoundException("User not found"));
        if(!passwordEncoder.matches(passwordDTO.getNewPassword(), user.getPassword())){
            throw new BadCredentialsException("Password incorrect");
        }
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
    }

    @Override
    public User fetchUserDetails(String mobile) {
        return userRepository.findByMobileNumber(mobile).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Override
    public Optional<User> fetchUserByMobileNumber(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }

    @Override
    public Long getUserCoins(String contact) {
        User user = userRepository.findByMobileNumber(contact).orElseThrow(()->new UserNotFoundException("User not found"));
        return user.getTotalPresentCoins();
    }
}