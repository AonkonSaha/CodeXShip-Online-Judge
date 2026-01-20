package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.UserCoinImageResponse;
import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserRegisterResponse;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.entity.User;

import java.util.List;

public interface UserMapper {

    public User toUser(UserRegisterRequest userRegisterRequest);

    public UserRegisterResponse toUserRegisterResponse(User user);


    List<UserResponse> toUsersResponse(List<User> ranking);

    UserCoinImageResponse toUserCoinImage(User userCoins);

    UserResponse toUserResponse(User user);
}
