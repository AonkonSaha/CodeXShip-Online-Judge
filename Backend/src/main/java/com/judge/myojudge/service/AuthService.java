package com.judge.myojudge.service;

import com.judge.myojudge.model.dto.LoginDTO;
import com.judge.myojudge.model.dto.PasswordDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.response.ApiResponse;

import java.util.Optional;

public interface AuthService {

    public User register(User user);

    public String login(LoginDTO loginDTO);

    public void logout(String token);

    public User saveUser(User user);

    public User updateUserDetails(String mobile, UpdateUserDTO updateUserDTO);

    public void updateUserPassword(String mobile, PasswordDTO passwordDTO);

    public User fetchUserDetails(String mobile);

    public Optional<User> fetchUserByMobileNumber(String mobile);

    public Long getUserCoins(String contact);
}
