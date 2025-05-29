package com.mytconvert.convertion.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Mp3ConvertionDTO extends ConvertionDTO {
    private String bitRate;

    public Mp3ConvertionDTO(Long id, String userName, String youtubeUrl, Long fileSize, LocalDateTime createdAt, String bitRate) {
        super(id, userName, youtubeUrl, fileSize, createdAt);
        this.bitRate = bitRate;
    }
}