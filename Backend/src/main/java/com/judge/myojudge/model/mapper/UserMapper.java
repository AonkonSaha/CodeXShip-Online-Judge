package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.RegisterUserDTO;
import com.judge.myojudge.model.dto.UserDTO;
import com.judge.myojudge.model.entity.User;

import java.util.List;

public interface UserMapper {

    public User toUser(RegisterUserDTO registerUserDTO);

    public RegisterUserDTO toUserRegisterDTO(User user);

    public UserDTO toUpdateUserDTO(User user);

    List<UserDTO> toUserDTO(List<User> ranking);

    UserDTO toUserUrlCoin(User userCoins);
}
