package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserUpdateRequest;
import com.judge.myojudge.model.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoMapperImp implements DtoMapper {
    @Override
    public UserRegisterRequest toRegisterDTO(UserUpdateRequest userUpdateRequest) {
        return UserRegisterRequest.builder()
                .mobile(userUpdateRequest.getMobile())
                .email(userUpdateRequest.getEmail())
                .gender(userUpdateRequest.getGender())
                .username(userUpdateRequest.getUsername())
                .build();
    }
}
