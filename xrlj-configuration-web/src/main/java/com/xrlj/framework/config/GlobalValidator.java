package com.xrlj.framework.config;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 全局验证器
 */
public class GlobalValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            throw new NullPointerException();
        }
    }
}
