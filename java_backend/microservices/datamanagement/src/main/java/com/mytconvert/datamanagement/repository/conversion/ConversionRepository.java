package com.mytconvert.datamanagement.repository.conversion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mytconvert.datamanagement.entity.conversion.Conversion;

import java.util.List;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    List<Conversion> findByUserId(Long userId);
    Page<Conversion> findByUserId(Long userId, Pageable pageable);
}