package com.mytconvert.usermanagement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.mytconvert.usermanagement.entity.UserType;

public class RequestValidator {
    // Regex para validação de email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    // Método para validar o email
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



    public static boolean isValidUserType(String userType) {
        for (UserType type : UserType.values()) {
            if (type.name().equalsIgnoreCase(userType)) {
                return true;
            }
        }
        return false;
    }

   

    public static void validateFields(Object requestObject, List<String> requiredFields) {
        try {
            for (String fieldName : requiredFields) {
                Field field = requestObject.getClass().getDeclaredField(fieldName);
                field.setAccessible(true); // Permite acessar campos privados
                Object value = field.get(requestObject);
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The required field '" + fieldName + "' is empty.");
                }

                if (fieldName.equals("email")) {
                    if (!(value instanceof String) || !isValidEmail((String) value)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The field 'email' must be a valid email address.");
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Field specified does not exist: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error accessing JSON fields: " + e.getMessage());
        }
    }

    // Método para validar campos em um Map
    public static void validateFieldsForMap(Map<String, String> requestMap, List<String> requiredFields) {
        for (String fieldName : requiredFields) {
            Object value = requestMap.get(fieldName);
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The required field '" + fieldName + "' is empty.");
            }

            if (fieldName.equals("email")) {
                if (!(value instanceof String) || !isValidEmail((String) value)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The field 'email' must be a valid email address.");
                }
            }
        }
    }
    
    public static void validatePasswordStrength(String password) {
        // Verifica se a senha tem entre 8 e 16 caracteres,
        // pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
        
        if (!password.matches(passwordRegex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "The provided password is too weak. Please ensure it includes at least one number, one uppercase letter, one lowercase letter, and have length between 8 and 16 characters.");
        }
    }
}
