package com.mytconvert.datamanagement.service.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.dto.UserConversionMetricsDTO;
import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.repository.conversion.ConversionRepository;
import com.mytconvert.security.utils.JwtUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    /**
     * Gets aggregated conversion metrics for all users.
     * 
     * @return List of UserConversionMetricsDTO with metrics for each user
     */
    public List<UserConversionMetricsDTO> getUserConversionMetrics() {
        List<Conversion> allConversions = conversionRepository.findAll();
        
        // Group conversions by user - using lambda instead of method reference
        Map<User, List<Conversion>> conversionsByUser = allConversions.stream()
                .collect(Collectors.groupingBy(conversion -> conversion.getUser()));
        
        List<UserConversionMetricsDTO> metrics = new ArrayList<>();
        
        // Calculate metrics for each user
        for (Map.Entry<User, List<Conversion>> entry : conversionsByUser.entrySet()) {
            User user = entry.getKey();
            List<Conversion> userConversions = entry.getValue();
            
            // Count MP3 and MP4 conversions
            long mp3Count = userConversions.stream()
                    .filter(c -> c.getFormat() == ConversionFormat.MP3)
                    .count();
            
            long mp4Count = userConversions.stream()
                    .filter(c -> c.getFormat() == ConversionFormat.MP4)
                    .count();
            
            // Calculate total minutes converted (length is in seconds)
            long totalMinutes = userConversions.stream()
                    .mapToLong(Conversion::getLength)
                    .sum() / 60;
            
            // Determine preferred format - if counts are equal, use MP3 as default
            ConversionFormat preferredFormat;
            if (mp3Count > mp4Count) {
                preferredFormat = ConversionFormat.MP3;
            } else if (mp4Count > mp3Count) {
                preferredFormat = ConversionFormat.MP4;
            } else {
                // Se ambos são iguais, verificar qual tem mais tempo total
                long mp3Seconds = userConversions.stream()
                        .filter(c -> c.getFormat() == ConversionFormat.MP3)
                        .mapToLong(Conversion::getLength)
                        .sum();
                
                long mp4Seconds = userConversions.stream()
                        .filter(c -> c.getFormat() == ConversionFormat.MP4)
                        .mapToLong(Conversion::getLength)
                        .sum();
                
                preferredFormat = mp3Seconds >= mp4Seconds ? ConversionFormat.MP3 : ConversionFormat.MP4;
            }
            
            // Create and add the metrics DTO
            UserConversionMetricsDTO dto = new UserConversionMetricsDTO(
                    user.getUsername(),
                    mp3Count,
                    mp4Count,
                    totalMinutes,
                    preferredFormat
            );
            
            metrics.add(dto);
        }
        
        return metrics;
    }
    
    /**
     * Gets paginated aggregated conversion metrics for all users.
     * 
     * @param pageable Pagination information
     * @return Page of UserConversionMetricsDTO with metrics for each user
     */
    public Page<UserConversionMetricsDTO> getUserConversionMetricsPaginated(Pageable pageable) {
        List<UserConversionMetricsDTO> allMetrics = getUserConversionMetrics();
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allMetrics.size());
        
        // Handle case where start is beyond list size
        if (start >= allMetrics.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, allMetrics.size());
        }
        
        List<UserConversionMetricsDTO> pagedMetrics = allMetrics.subList(start, end);
        
        return new PageImpl<>(pagedMetrics, pageable, allMetrics.size());
    }
}