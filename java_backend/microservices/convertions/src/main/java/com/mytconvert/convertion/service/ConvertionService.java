package com.mytconvert.convertion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.repository.ConvertionRepository;

@Service
public class ConvertionService {

    private final ConvertionRepository convertionRepository;

    @Autowired
    public ConvertionService(ConvertionRepository convertionRepository) {
        this.convertionRepository = convertionRepository;
    }

    @Transactional
    public Convertion createConvertion(Convertion convertion) {
        
        return convertionRepository.save(convertion);
    }

    public Page<Convertion> getConvertions(Pageable pageable) {
        // Adapt this to your specific filtering requirements
        return convertionRepository.findAll(pageable);  // Simplest case:  Get all
        //OR if you want a specific type, deactivated, etc.

       // Example (adapt as needed): return convertionRepository.findByTypeNot(pageable, ConvertionType.SYSTEM);
    }
}