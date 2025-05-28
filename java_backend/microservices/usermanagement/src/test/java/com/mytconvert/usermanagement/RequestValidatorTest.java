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

        // Getters e Setters
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
        assertTrue(RequestValidator.isValidEmail("teste@example.br")); // Deve retornar true
        assertFalse(RequestValidator.isValidEmail("invalido@exemplo@com")); // Deve retornar false
        assertFalse(RequestValidator.isValidEmail("invalido@dominio")); // Deve retornar false
        assertFalse(RequestValidator.isValidEmail("sem-arroba.com")); // Deve retornar false
    }

    @Test
    public void testValidatePasswordStrength_ThrowsException_WhenPasswordIsWeak() {
        // Arrange
        String weakPassword = "weak"; // Senha que não atende aos critérios

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validatePasswordStrength(weakPassword);
        });

        // Verifica se a exceção lançada é a esperada
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("The provided password is too weak. Please ensure it includes at least one number, one uppercase letter, one lowercase letter, and have length between 8 and 16 characters.", 
                     exception.getReason());
    }

    @Test
    public void testValidatePasswordStrength_ThrowsException_WhenPasswordIsTooShort() {
        // Arrange
        String shortPassword = "Short1!"; // Senha que tem menos de 8 caracteres

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validatePasswordStrength(shortPassword);
        });

        // Verifica se a exceção lançada é a esperada
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("The provided password is too weak. Please ensure it includes at least one number, one uppercase letter, one lowercase letter, and have length between 8 and 16 characters.", 
                     exception.getReason());
    }

    @Test
    public void testValidatePasswordStrength_ThrowsException_WhenPasswordHasNoNumber() {
        // Arrange
        String passwordWithoutNumber = "Password!"; // Senha sem número

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validatePasswordStrength(passwordWithoutNumber);
        });

        // Verifica se a exceção lançada é a esperada
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("The provided password is too weak. Please ensure it includes at least one number, one uppercase letter, one lowercase letter, and have length between 8 and 16 characters.", 
                     exception.getReason());
    }

    @Test
    public void testValidatePasswordStrength_Passes_WhenPasswordIsStrong() {
        // Arrange
        String strongPassword = "Strong1!"; // Senha que atende aos critérios

        // Act
        RequestValidator.validatePasswordStrength(strongPassword);

        // Assert: Não deve lançar exceção
    }

    @Test
    public void testValidUserType() {
        assertTrue(RequestValidator.isValidUserType("USER")); // Deve retornar true
        assertTrue(RequestValidator.isValidUserType("ADMIN")); // Deve retornar true
        assertTrue(RequestValidator.isValidUserType("admin")); // Deve retornar true (case insensitive)
        assertFalse(RequestValidator.isValidUserType("GUEST")); // Deve retornar false
        assertFalse(RequestValidator.isValidUserType("")); // Deve retornar false (string vazia)
        assertFalse(RequestValidator.isValidUserType(null)); // Deve retornar false (null)
    }

        @Test
    public void testValidateFields_ThrowException_WhenFieldIsNull() {
        // Arrange
        TestRequest request = new TestRequest(null, "valid.email@example.br");
        List<String> requiredFields = Arrays.asList("name", "email");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validateFields(request, requiredFields);
        });

        // Verifica se a exceção lançada é a esperada
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("The required field 'name' is empty.", exception.getReason());
    }

    @Test
    public void testValidateFields_ThrowException_WhenFieldDoesNotExist() {
        // Arrange
        TestRequest request = new TestRequest("Valid Name", "valid.email@example.br");
        List<String> requiredFields = Arrays.asList("name", "email", "nonExistentField");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            RequestValidator.validateFields(request, requiredFields);
        });

        // Verifica se a exceção lançada é a esperada
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        
        String reason = Optional.ofNullable(exception.getReason()).orElse("");
        assertTrue(reason.contains("Field specified does not exist"), 
                "Expected error message not found in: " + reason);
    }

    @Test
    void testValidateFields_ValidEmail() {
        TestRequest request = new TestRequest("John Doe", "john.doe@example.br");
        List<String> requiredFields = Arrays.asList("name", "email");

        assertDoesNotThrow(() -> RequestValidator.validateFields(request, requiredFields));
    }
}