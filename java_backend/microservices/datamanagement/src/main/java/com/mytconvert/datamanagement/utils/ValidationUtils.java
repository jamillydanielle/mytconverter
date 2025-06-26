package com.mytconvert.datamanagement.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidationUtils {

    public static void validatePositiveLong(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be a positive number.");
        }
    }
}