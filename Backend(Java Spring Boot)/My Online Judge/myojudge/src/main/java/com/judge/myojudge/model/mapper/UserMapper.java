package com.judge.myojudge.model.mapper;

import com.judge.myojudge.enums.Role;
import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.model.dto.UserDTO;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;

import java.util.Set;

public interface UserMapper {

    public User toUser(RegisterDTO registerDTO);

    public RegisterDTO toUserRegisterDTO(User user);

    public UserDTO toUpdateUserDTO(User user);
}
