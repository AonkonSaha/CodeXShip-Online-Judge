package com.judge.myojudge.validation;

import com.judge.myojudge.model.dto.ProblemRequest;

public interface ValidationService {
    void validateProblemDetails(ProblemRequest problemRequest);
}
