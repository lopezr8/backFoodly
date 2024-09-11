package com.rafael.foodly.foodly.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UniqueIdentificationValidation.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UniqueIdentification {
    String message() default "This identification is already in use";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
