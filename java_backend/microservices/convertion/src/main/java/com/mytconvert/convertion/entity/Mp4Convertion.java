package com.mytconvert.convertion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("MP4")
public class Mp4Convertion extends Convertion {

    @Column(name = "video_resolution")
    private String videoResolution;

    public Mp4Convertion(String videoResolution) {
        this.videoResolution = videoResolution;
    }
}