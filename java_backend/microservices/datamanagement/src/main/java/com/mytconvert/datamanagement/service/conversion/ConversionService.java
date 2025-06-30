package com.mytconvert.datamanagement.service.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.repository.conversion.ConversionRepository;
import com.mytconvert.security.utils.JwtUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ConversionService {
    @Autowired
    private final ConversionRepository conversionRepository;

    public ConversionService(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    /**
     * Creates a new conversion with the legacy parameters
     * @deprecated Use the new createConversion method with youtubeVideoName and youtubeUrl
     */
    @Deprecated
    public Conversion createConversion(User requester, String internalFileName, String format, Long length) {
        // For backward compatibility, create with default values for new fields
        return createConversion(requester, internalFileName, "Unknown Video", "Unknown URL", format, length);
    }

    /**
     * Creates a new conversion with all required parameters.
     * If a conversion with the same URL, user, and format already exists, returns the existing conversion.
     * 
     * @param requester The user requesting the conversion
     * @param internalFileName The internal file name
     * @param youtubeVideoName The YouTube video name
     * @param youtubeUrl The YouTube video URL
     * @param format The conversion format
     * @param length The length of the video in seconds
     * @return The created or existing Conversion entity
     */
    public Conversion createConversion(User requester, String internalFileName, String youtubeVideoName, 
                                      String youtubeUrl, String format, Long length) {
        // Convert format string to enum
        ConversionFormat conversionFormat = ConversionFormat.valueOf(format);
        
        // Check if a conversion with the same URL, user, and format already exists
        Optional<Conversion> existingConversion = conversionRepository.findByYoutubeUrlAndUserIdAndFormat(
            youtubeUrl, requester.getId(), conversionFormat);
        
        // If a duplicate exists, return it
        if (existingConversion.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conversao ja cadastrada");
        }
        
        // Otherwise, create a new conversion
        Conversion addConversion = new Conversion(requester, internalFileName, youtubeVideoName, youtubeUrl, conversionFormat, length);
        return conversionRepository.save(addConversion);
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