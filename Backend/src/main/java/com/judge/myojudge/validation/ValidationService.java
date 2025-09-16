package com.judge.myojudge.validation;

import com.judge.myojudge.model.dto.*;

public interface ValidationService {
    void validateUserDetails(RegisterDTO registerDTO);
    void validateUserDetails(UpdateUserDTO updateUserDTO);
    void validateLoginDetails(LoginDTO loginDTO);
    void validateUserPassword(PasswordDTO passwordDTO);
    void validateProblemDetails(ProblemDTO problemDTO);
}
