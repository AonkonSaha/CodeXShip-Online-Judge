package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.LoginDTO;
import com.judge.myojudge.model.dto.PasswordDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;
import com.judge.myojudge.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface AuthService {

    public User register(User user);

    public String login(LoginDTO loginDTO);

    public void logout(String mobileOrEmail,String token);

    public User saveUser(User user);

    public User updateUserDetails(String mobileOrEmail, UpdateUserDTO updateUserDTO);

    public void updateUserPassword(String mobileOrEmail, PasswordDTO passwordDTO);

    public User fetchUserDetails(String mobileOrEmail);

    public Optional<User> fetchUserByMobileNumber(String mobile);

    public User getUserCoinWithImgUrl(String mobileOrEmail);

    User fetchUserDetailsByUsername(String username,Long userId);

    String updateProfileImage(MultipartFile file) throws Exception;

    Page<User> getUsers(String search, Pageable pageable);

    void deleteUser(String email);

    void updateUserDetailsByAdmin(UpdateUserDTO updateUserDTO);

    String loginByGoogle(String email, String name, String picture);

    User fetchUserByEmail(String email);
}
