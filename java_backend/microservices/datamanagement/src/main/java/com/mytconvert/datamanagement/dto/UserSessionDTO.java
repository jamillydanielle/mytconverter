package com.mytconvert.datamanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDTO {
    private Long userId;
    private String username;
    private LocalDateTime lastSession;
}