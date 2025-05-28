package com.mytconvert.usermanagement.service;

public class PasswordNeedsChangeException extends RuntimeException {
    private final String token;

    public PasswordNeedsChangeException(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
