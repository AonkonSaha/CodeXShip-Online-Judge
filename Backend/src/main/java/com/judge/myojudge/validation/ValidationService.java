package com.judge.myojudge.validation;

import com.judge.myojudge.model.dto.LoginDTO;
import com.judge.myojudge.model.dto.PasswordDTO;
import com.judge.myojudge.model.dto.RegisterDTO;
import com.judge.myojudge.model.dto.UpdateUserDTO;

public interface ValidationService {
    void validateUserDetails(RegisterDTO registerDTO);
    void validateUserDetails(UpdateUserDTO updateUserDTO);


    void validateLoginDetails(LoginDTO loginDTO);

    void validateUserPassword(PasswordDTO passwordDTO);
}
