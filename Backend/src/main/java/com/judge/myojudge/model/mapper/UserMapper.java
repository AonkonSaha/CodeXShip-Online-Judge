package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.model.dto.UserDTO;
import com.judge.myojudge.model.entity.User;

public interface UserMapper {

    public User toUser(RegisterDTO registerDTO);

    public RegisterDTO toUserRegisterDTO(User user);

    public UserDTO toUpdateUserDTO(User user);
}
