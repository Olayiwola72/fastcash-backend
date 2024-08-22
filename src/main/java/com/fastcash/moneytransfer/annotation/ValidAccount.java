package com.fastcash.moneytransfer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fastcash.moneytransfer.validation.ValidAccountValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { ValidAccountValidator.class })
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccount {
    String message() default "Invalid Account";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}