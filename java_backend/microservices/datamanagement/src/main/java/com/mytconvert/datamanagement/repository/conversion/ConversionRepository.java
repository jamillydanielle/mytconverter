package com.mytconvert.datamanagement.repository.conversion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mytconvert.datamanagement.entity.conversion.Conversion;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    Conversion findByInternalFileName(String fileName);
    
    List<Conversion> findByRequesterId(Long requesterId);
}