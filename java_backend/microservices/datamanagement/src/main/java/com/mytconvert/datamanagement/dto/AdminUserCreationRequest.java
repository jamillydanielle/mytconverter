package com.mytconvert.datamanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserCreationRequest {
    private String name;
    private String email;
    private String password;
}