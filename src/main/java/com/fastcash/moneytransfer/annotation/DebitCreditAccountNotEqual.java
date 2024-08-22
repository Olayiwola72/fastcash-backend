package com.fastcash.moneytransfer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fastcash.moneytransfer.validation.DebitCreditAccountNotEqualValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { DebitCreditAccountNotEqualValidator.class })
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DebitCreditAccountNotEqual {
    String message() default "Debit account must not be equal to credit account";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}