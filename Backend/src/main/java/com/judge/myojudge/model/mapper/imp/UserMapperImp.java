package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.enums.Role;
import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.model.dto.UserDTO;
import com.judge.myojudge.model.entity.User;
import com.judge.myojudge.model.entity.UserRole;
import com.judge.myojudge.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapperImp implements UserMapper {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User toUser(RegisterDTO registerDTO) {
        UserRole userRole = new UserRole();
        userRole.setRoleName(Role.NORMAL_USER.name());
        User user= User.builder()
               .username(registerDTO.getUsername())
               .email(registerDTO.getEmail())
               .mobileNumber(registerDTO.getMobile())
               .userRoles(Set.of(userRole))
               .password(passwordEncoder.encode(registerDTO.getPassword()))
               .build();
        userRole.setUsers(Set.of(user));
        return user;
    }

    @Override
    public RegisterDTO toUserRegisterDTO(User user) {
        return RegisterDTO.builder()
                .username(user.getUsername())
                .mobile(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserDTO toUpdateUserDTO(User user) {
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .city(user.getCity())
                .country(user.getCountry())
                .gender(user.getGender())
                .mobileNumber(user.getMobileNumber())
                .state(user.getState())
                .imageUrl(user.getImageUrl())
                .githubUrl(user.getGithubUrl())
                .dateOfBirth(user.getDateOfBirth())
                .linkedinUrl(user.getLinkedinUrl())
                .facebookUrl(user.getFacebookUrl())
                .postalCode(user.getPostalCode())
                .build();
    }

}
