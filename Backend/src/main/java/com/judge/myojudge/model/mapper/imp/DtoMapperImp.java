package com.judge.myojudge.model.mapper.imp;

import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;
import com.judge.myojudge.model.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoMapperImp implements DtoMapper {
    @Override
    public RegisterDTO toRegisterDTO(UpdateUserDTO updateUserDTO) {
        return RegisterDTO.builder()
                .mobile(updateUserDTO.getMobileNumber())
                .email(updateUserDTO.getEmail())
                .gender(updateUserDTO.getGender())
                .username(updateUserDTO.getUserName())
                .build();
    }
}
