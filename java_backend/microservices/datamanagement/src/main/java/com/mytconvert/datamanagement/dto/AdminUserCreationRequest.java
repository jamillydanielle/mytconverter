package com.mytconvert.datamanagement.dto;

import lombok.Data;

@Data
public class AdminUserCreationRequest {
    private String name;
    private String email;
    private String password;
}