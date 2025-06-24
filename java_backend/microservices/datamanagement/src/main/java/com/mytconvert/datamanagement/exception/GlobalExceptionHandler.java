package com.mytconvert.datamanagement.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", String.valueOf(ex.getStatusCode().value()));
        errorResponse.put("error", ex.getReason());
        
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String fieldName = ex.getName();
        String invalidValue = Optional.ofNullable(ex.getValue())
                                    .map(Object::toString)
                                    .orElse("null");
        Class<?> requiredType = ex.getRequiredType();
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Valor invalido para o campo: " + fieldName);
        errorDetails.put("providedValue", invalidValue);
        
        if (requiredType != null && requiredType.isEnum()) {
            Object[] enumConstants = requiredType.getEnumConstants();
            errorDetails.put("validOptions", enumConstants);
        }
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
        if(ex.getMessage().contains("No enum constant")){
            errorResponse.put("error", "Um ou mais valores enum estao incorretos, por favor verifique o corpo da requisicao.");
        }else{
            errorResponse.put("error", ex.getMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        Map<String, Object> errorDetails = new HashMap<>();
        
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            Class<?> targetType = ife.getTargetType();
            
            errorDetails.put("error", "Valor invalido para conversao de tipo");
            errorDetails.put("providedValue", ife.getValue());
            
            if (targetType != null && targetType.isEnum()) {
                Object[] validValues = targetType.getEnumConstants();
                errorDetails.put("validOptions", validValues);
            }
        } else {
            errorDetails.put("error", ex.getMessage());
        }
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }    
}