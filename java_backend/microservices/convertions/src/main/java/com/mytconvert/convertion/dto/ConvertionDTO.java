package com.mytconvert.convertion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ConvertionDTO {
    private Long id;
    private String userName;
    private String youtubeUrl;
    private Long fileSize;
    private LocalDateTime createdAt;

    public ConvertionDTO(Long id, String userName, String youtubeUrl, Long fileSize, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.youtubeUrl = youtubeUrl;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }
}