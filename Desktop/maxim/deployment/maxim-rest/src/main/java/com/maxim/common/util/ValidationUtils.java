package com.maxim.common.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class ValidationUtils {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> List<String> validateObject(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        return getErrorMessage(violations);
    }

    public static <T> List<String> validateCollection(Collection<T> collection, Class<?>... groups) {
        for (T object : collection) {
            List<String> errors = validateObject(object, groups);
            if (!errors.isEmpty()) {
                return errors;
            }
        }
        return Collections.emptyList();
    }

    public static <T> List<String> validateProperty(T object, String propertyName, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(object, propertyName, groups);
        return getErrorMessage(violations);
    }

    public static <T> List<String> validateParameters(T object, Method method, Object[] parameterValues, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.forExecutables().validateParameters(object, method, parameterValues, groups);
        return getErrorMessage(violations);
    }

    public static <T> List<String> validateReturnValue(T object, Method method, Object returnValue, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.forExecutables().validateReturnValue(object, method, returnValue, groups);
        return getErrorMessage(violations);
    }

    public static <T> List<String> getErrorMessage(Set<ConstraintViolation<T>> violations) {
        if (violations == null || violations.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (ConstraintViolation<T> violation : violations) {
            list.add(violation.getPropertyPath() + ":" + violation.getMessage());
        }
        return list;
    }

    public static Validator getValidator() {
        return validator;
    }

}
