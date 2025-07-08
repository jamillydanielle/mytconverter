package com.mytconvert.datamanagement.repository.conversion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    List<Conversion> findByUserId(Long userId);
    Page<Conversion> findByUserId(Long userId, Pageable pageable);
    
    // New method to find conversions by YouTube URL, user ID, and format
    Optional<Conversion> findByYoutubeUrlAndUserIdAndFormat(String youtubeUrl, Long userId, ConversionFormat format);
}