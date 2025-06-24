package com.mytconvert.datamanagement.service.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import com.mytconvert.datamanagement.repository.conversion.ConversionRepository;
import com.mytconvert.datamanagement.repository.user.UserRepository;
import com.mytconvert.security.utils.JwtUtils;

@Service
public class ConversionService {
    
    private final ConversionRepository conversionRepository;

    @Autowired
    public ConversionService(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    public Conversion createConversion(String internalFileName, String format) {
        return conversionRepository.save(new Conversion(JwtUtils.getCurrentUser().get(), internalFileName, ConversionFormat.valueOf(format)));
    }
}
