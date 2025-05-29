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
@DiscriminatorValue("MP3")
public class Mp3Convertion extends Convertion {

    @Column(name = "bit_rate")
    private String bitRate;

    public Mp3Convertion(String bitRate) {
        this.bitRate = bitRate;
    }
}