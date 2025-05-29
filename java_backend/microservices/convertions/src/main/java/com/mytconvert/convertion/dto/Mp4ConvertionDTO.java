package com.mytconvert.convertion.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Mp4ConvertionDTO extends ConvertionDTO {
    private String videoResolution;

    public Mp4ConvertionDTO(Long id, String userName, String youtubeUrl, Long fileSize, LocalDateTime createdAt, String videoResolution) {
        super(id, userName, youtubeUrl, fileSize, createdAt);
        this.videoResolution = videoResolution;
    }
}