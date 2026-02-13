package com.bank.util;

import com.bank.exception.ArgumentValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.Set;
import java.util.stream.Collectors;

public final class ValidationUtils {
    private ValidationUtils() {}

    public static void validateParameters(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Set<String> messages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toSet());
            throw new ArgumentValidationException(messages.toString());
        }
    }

}
