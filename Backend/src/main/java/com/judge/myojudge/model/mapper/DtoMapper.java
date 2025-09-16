package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;

public interface DtoMapper {
    RegisterDTO toRegisterDTO(UpdateUserDTO updateUserDTO);
}
