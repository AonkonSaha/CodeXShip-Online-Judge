package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.RegisterUserDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;

public interface DtoMapper {
    RegisterUserDTO toRegisterDTO(UpdateUserDTO updateUserDTO);
}
