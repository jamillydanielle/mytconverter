package com.mytconvert.datamanagement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.user.UserType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class RequestValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
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
                field.setAccessible(true);
                Object value = field.get(requestObject);
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O campo obrigatorio '" + fieldName + "' esta vazio.");
                }

                if (fieldName.equals("email")) {
                    if (!(value instanceof String) || !isValidEmail((String) value)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "O campo 'email' deve ter um email valido.");
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "O campo especificado nao existe: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao acessar os campos do Json: " + e.getMessage());
        }
    }

    public static void validateFieldsForMap(Map<String, Object> requestMap, List<String> requiredFields) {
        for (String fieldName : requiredFields) {
            Object value = requestMap.get(fieldName);
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O campo obrigatorio '" + fieldName + "' esta vazio.");
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
       
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
        
        if (!password.matches(passwordRegex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "A senha fornecida e muito fraca. Por favor garanta que a senha inclua pelo menos um numero, uma letra maiuscula, uma letra minuscula e tenha entre 8 e 16 caracteres.");
        }
    }
}
