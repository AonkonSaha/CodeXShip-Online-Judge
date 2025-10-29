package com.judge.myojudge.validation.imp;

import com.judge.myojudge.model.dto.RegisterUserDTO;
import com.judge.myojudge.model.dto.RegisterUserDTO;
import com.judge.myojudge.validation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterUserDTO> {
    @Override
    public boolean isValid(RegisterUserDTO value, ConstraintValidatorContext context) {
        return value.getPassword().equals(value.getConfirmPassword());
    }
}
