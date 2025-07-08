package com.mytconvert.datamanagement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.utils.ValidationUtils;

public class ValidationUtilsTest {

    @Test
    public void testValidatePositiveLong_ValidValue() {
        assertDoesNotThrow(() -> ValidationUtils.validatePositiveLong(10L, "length"));
    }

    @Test
    public void testValidatePositiveLong_NullValue() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ValidationUtils.validatePositiveLong(null, "length");
        });
        assertEquals("length must be a positive number.", exception.getReason());
    }

    @Test
    public void testValidatePositiveLong_NegativeValue() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ValidationUtils.validatePositiveLong(-5L, "length");
        });
        assertEquals("length must be a positive number.", exception.getReason());
    }

    @Test
    public void testValidatePositiveLong_ZeroValue() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ValidationUtils.validatePositiveLong(0L, "length");
        });
        assertEquals("length must be a positive number.", exception.getReason());
    }
}