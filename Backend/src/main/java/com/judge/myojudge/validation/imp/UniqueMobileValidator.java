package com.judge.myojudge.validation.imp;

import com.judge.myojudge.repository.UserRepo;
import com.judge.myojudge.validation.UniqueMobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniqueMobileValidator implements ConstraintValidator<UniqueMobile,String> {
    private final UserRepo userRepo;
    @Override
    public boolean isValid(String mobile, ConstraintValidatorContext context) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return true;
        }
        return !userRepo.existsByMobileNumber(mobile);
    }
}
