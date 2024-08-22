package com.fastcash.moneytransfer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fastcash.moneytransfer.validation.ValidAdminValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidAdminValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAdmin {
	String message() default "User type is not Internal";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
