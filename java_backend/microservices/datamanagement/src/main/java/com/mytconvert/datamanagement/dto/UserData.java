package com.mytconvert.datamanagement.dto;

import com.mytconvert.datamanagement.entity.user.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserData {
    // Construtor explícito com todos os campos
    public UserData(long id, String name, String email, UserType type, LocalDateTime deactivatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.deactivatedAt = deactivatedAt;
    }
    
    // Construtor com id, name, email, type
    public UserData(Long id, String name, String email, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }
    
    // Construtor com name, email, type
    public UserData(String name, String email, UserType type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }
    
    // Construtor com name, email - usado pelo getCurrentUserData
    public UserData(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    private long id;
    private String name;
    private String email;
    private UserType type;
    private LocalDateTime deactivatedAt;
}