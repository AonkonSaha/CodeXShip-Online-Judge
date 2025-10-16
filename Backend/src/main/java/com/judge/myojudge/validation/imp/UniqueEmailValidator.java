package com.judge.myojudge.validation.imp;

import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserRepo userRepo;
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userRepo.existsByEmail(value);
    }
}
