package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.UserCoinImageResponse;
import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserRegisterResponse;
import com.judge.myojudge.model.dto.UserResponse;
import com.judge.myojudge.model.entity.User;

import java.util.List;

public interface UserMapper {

    User toUser(UserRegisterRequest userRegisterRequest);

    UserRegisterResponse toUserRegisterResponse(User user);

    List<UserResponse> toUsersResponse(List<User> users);

    UserCoinImageResponse toUserCoinImage(User userCoins);

    UserResponse toUserResponse(User user);
}
