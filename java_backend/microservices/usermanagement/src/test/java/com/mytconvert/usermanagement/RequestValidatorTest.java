package com.mytconvert.usermanagement;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.usermanagement.utils.RequestValidator;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RequestValidatorTest {
    public static class TestRequest {
        private String name;
        private String email;

        public TestRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @Test
    public void testValidEmail() {
        assertTrue(RequestValidator.isValidEmail("teste@example.com"));
        assertFalse(RequestValidator.isValidEmail("invalido@exemplo@com")); 
        assertFalse(RequestValidator.isValidEmail("invalido@dominio")); 
        assertFalse(RequestValidator.isValidEmail("sem-arroba.com")); 
    }

    @Test
    public void testValidatePasswordStrength_ThrowsException_WhenPasswordIsWeak() {
        String weakPassword = "weak";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validatePasswordStrength(weakPassword);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha fornecida e muito fraca. Por favor garanta que a senha incua pelo menos um numero, uma letra maiuscula, uma letra minuscula e tenha entre 8 e 16 caracteres.", 
                     exception.getReason());
    }

    @Test
    public void testValidatePasswordStrength_ThrowsException_WhenPasswordIsTooShort() {
        String shortPassword = "Short1!";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validatePasswordStrength(shortPassword);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha fornecida e muito fraca. Por favor garanta que a senha incua pelo menos um numero, uma letra maiuscula, uma letra minuscula e tenha entre 8 e 16 caracteres.", 
                     exception.getReason());
    }

    @Test
    public void testValidatePasswordStrength_ThrowsException_WhenPasswordHasNoNumber() {
        String passwordWithoutNumber = "Password!";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validatePasswordStrength(passwordWithoutNumber);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("A senha fornecida e muito fraca. Por favor garanta que a senha incua pelo menos um numero, uma letra maiuscula, uma letra minuscula e tenha entre 8 e 16 caracteres.", 
                     exception.getReason());
    }

    @Test
    public void testValidatePasswordStrength_Passes_WhenPasswordIsStrong() {
        String strongPassword = "Strong1!";

        RequestValidator.validatePasswordStrength(strongPassword);

    }

    @Test
    public void testValidUserType() {
        assertTrue(RequestValidator.isValidUserType("USER")); 
        assertTrue(RequestValidator.isValidUserType("ADMIN")); 
        assertTrue(RequestValidator.isValidUserType("admin"));
        assertFalse(RequestValidator.isValidUserType("GUEST"));
        assertFalse(RequestValidator.isValidUserType(""));
        assertFalse(RequestValidator.isValidUserType(null)); 
    }

        @Test
    public void testValidateFields_ThrowException_WhenFieldIsNull() {
        
        TestRequest request = new TestRequest(null, "valid.email@example.com");
        List<String> requiredFields = Arrays.asList("name", "email");


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validateFields(request, requiredFields);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("O campo obrigatorio 'name' esta vazio.", exception.getReason());
    }

    @Test
    public void testValidateFields_ThrowException_WhenFieldDoesNotExist() {
        TestRequest request = new TestRequest("Valid Name", "valid.email@example.com");
        List<String> requiredFields = Arrays.asList("name", "email", "nonExistentField");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validateFields(request, requiredFields);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        
        String reason = Optional.ofNullable(exception.getReason()).orElse("");
        assertTrue(reason.contains("Field specified does not exist"), 
                "Expected error message not found in: " + reason);
    }

    @Test
    void testValidateFields_ValidEmail() {
        TestRequest request = new TestRequest("John Doe", "john.doe@example.com");
        List<String> requiredFields = Arrays.asList("name", "email");

        assertDoesNotThrow(() -> RequestValidator.validateFields(request, requiredFields));
    }
}