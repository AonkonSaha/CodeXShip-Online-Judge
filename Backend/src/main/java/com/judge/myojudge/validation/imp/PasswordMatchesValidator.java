package com.judge.myojudge.validation.imp;

import com.judge.myojudge.model.dto.UserRegisterRequest;
import com.judge.myojudge.validation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterRequest> {
    @Override
    public boolean isValid(UserRegisterRequest value, ConstraintValidatorContext context) {
        return value.getPassword().equals(value.getConfirmPassword());
    }
}
