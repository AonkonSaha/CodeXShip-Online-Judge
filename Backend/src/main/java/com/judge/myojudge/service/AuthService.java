package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.LoginRequest;
import com.judge.myojudge.model.dto.PasswordRequest;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.dto.UserUpdateRequest;
import com.judge.myojudge.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface AuthService {

     String login(LoginRequest loginRequest);

     void logout(String mobileOrEmail,String token);

     User saveUser(User user);

     User updateUserDetails(String mobileOrEmail, UserUpdateRequest userUpdateRequest);

     void updateUserPassword(String mobileOrEmail, PasswordRequest passwordRequest);
     User getUserByMobileOrEmail(String mobileOrEmail);

     Optional<User> fetchUserByMobileNumber(String mobile);

     User getUserById(Long userId);

     String updateProfileImage(MultipartFile file) throws Exception;

     Page<User> getUsers(String search, Pageable pageable);

    void deleteUser(String email);

    void updateUserDetailsByAdmin(UserUpdateRequest userUpdateRequest);

    String loginByGoogle(String email, String name, String picture);

    User fetchUserByEmail(String email);

    UserResponse fetchUserByProblemSolvedHistory(String mobileOrEmail);

    boolean isExitsUserByEmail(String email);
}
