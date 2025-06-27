package com.mytconvert.datamanagement.service.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import com.mytconvert.datamanagement.entity.user.User;
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

    public Conversion createConversion(User requester, String internalFileName, String format, Long length) {
        return conversionRepository.save(new Conversion(requester, internalFileName, ConversionFormat.valueOf(format), length));
    }

    public List<Conversion> getAllConversions() {
        return conversionRepository.findAll();
    }

    public Page<Conversion> getAllConversionsPaginated(Pageable pageable) {
        return conversionRepository.findAll(pageable);
    }

    public List<Conversion> getConversionsByUserId(Long userId) {
        return conversionRepository.findByUserId(userId);
    }
    
    public Page<Conversion> getConversionsByUserIdPaginated(Long userId, Pageable pageable) {
        return conversionRepository.findByUserId(userId, pageable);
    }
}