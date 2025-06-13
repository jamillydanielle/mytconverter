package com.mytconvert.datamanagement.repository.convertion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.convertion.Convertion;

@Repository
public interface ConvertionRepository extends JpaRepository<Convertion, Long> {
    Convertion findByFileName(String fileName);
    default Convertion findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversao nao encontrada"));
    }
}