package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.RegisterUserDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;
import com.judge.myojudge.model.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoMapperImp implements DtoMapper {
    @Override
    public RegisterUserDTO toRegisterDTO(UpdateUserDTO updateUserDTO) {
        return RegisterUserDTO.builder()
                .mobile(updateUserDTO.getMobile())
                .email(updateUserDTO.getEmail())
                .gender(updateUserDTO.getGender())
                .username(updateUserDTO.getUsername())
                .build();
    }
}
