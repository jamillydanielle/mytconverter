package com.mytconvert.datamanagement.dto;

import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConversionMetricsDTO {
    private String username;
    private long totalMp3Conversions;
    private long totalMp4Conversions;
    private long totalMinutesConverted;
    private ConversionFormat preferredFormat;
}