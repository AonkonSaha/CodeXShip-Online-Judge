package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserRegisterResponse;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.entity.User;

import java.util.List;

public interface UserMapper {

    public User toUser(UserRegisterRequest userRegisterRequest);

    public UserRegisterResponse toUserRegisterResponse(User user);

    public UserResponse toUpdateUserDTO(User user);

    List<UserResponse> toUsersResponse(List<User> ranking);

    UserResponse toUserUrlCoin(User userCoins);

    UserResponse toUserResponse(User user);
}
