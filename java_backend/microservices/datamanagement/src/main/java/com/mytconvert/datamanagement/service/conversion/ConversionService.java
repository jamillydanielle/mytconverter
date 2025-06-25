package com.mytconvert.datamanagement.service.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import com.mytconvert.datamanagement.repository.conversion.ConversionRepository;
import com.mytconvert.security.utils.JwtUtils;

import java.util.List;

@Service
public class ConversionService {
    @Autowired
    private final ConversionRepository conversionRepository;

    
    public ConversionService(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    public Conversion createConversion(String internalFileName, String format, Long length) {
        return conversionRepository.save(new Conversion(JwtUtils.getCurrentUser().get(), internalFileName, ConversionFormat.valueOf(format), length));
    }

    public List<Conversion> getAllConversions() {
        return conversionRepository.findAll();
    }

    public List<Conversion> getConversionsByUserId(Long userId) {
        return conversionRepository.findByRequesterId(userId);
    }
}