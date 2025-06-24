package com.mytconvert.datamanagement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.service.conversion.ConversionService;

public class ConversionServiceTest {

    @Autowired
    private ConversionService conversionService;

    @Test
    public void testCreateConversion() {
        Conversion conversion = conversionService.createConversion("testFile.mp4", "mp4", 120L);
        assertNotNull(conversion);
        assertEquals("testFile.mp4", conversion.getInternalFileName());
        assertEquals(120L, conversion.getLength());
    }
}
