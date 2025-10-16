package com.judge.myojudge.validation;

import com.judge.myojudge.validation.imp.UniqueMobileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueMobileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueMobile {
    String message() default "Mobile number already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
