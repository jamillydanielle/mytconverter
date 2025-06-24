package com.mytconvert.datamanagement.repository.conversion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.conversion.Conversion;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {
    Conversion findByInternalFileName(String fileName);
    
    default Conversion findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversao nao encontrada"));
    }
}