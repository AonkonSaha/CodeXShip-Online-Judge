package com.judge.myojudge.validation;

import com.judge.myojudge.model.dto.ProblemDTO;

public interface ValidationService {
    void validateProblemDetails(ProblemDTO problemDTO);
}
