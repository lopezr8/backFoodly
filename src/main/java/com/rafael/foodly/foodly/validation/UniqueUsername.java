package com.rafael.foodly.foodly.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Constraint(validatedBy = UniqueUsernameValidation.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UniqueUsername {
    String message() default "This username is already in use";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
