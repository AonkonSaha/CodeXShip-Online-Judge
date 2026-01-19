package com.judge.myojudge.model.mapper;

import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.model.dto.UserUpdateRequest;

public interface DtoMapper {
    UserRegisterRequest toRegisterDTO(UserUpdateRequest userUpdateRequest);
}
