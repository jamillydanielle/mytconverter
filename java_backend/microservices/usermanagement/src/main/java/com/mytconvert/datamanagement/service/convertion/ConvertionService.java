package com.mytconvert.datamanagement.service.convertion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mytconvert.datamanagement.entity.convertion.Convertion;
import com.mytconvert.datamanagement.entity.convertion.ConvertionFormat;
import com.mytconvert.datamanagement.repository.convertion.ConvertionRepository;
import com.mytconvert.datamanagement.repository.user.UserRepository;
import com.mytconvert.security.utils.JwtUtils;

@Service
public class ConvertionService {
    
    private final ConvertionRepository convertionRepository;

    @Autowired
    public ConvertionService(ConvertionRepository convertionRepository) {
        this.convertionRepository = convertionRepository;
    }

    public Convertion createConvertion(String internalFileName, String format) {
        Convertion newConvertion = convertionRepository.save(new Convertion(JwtUtils.getCurrentUser().get(), internalFileName, ConvertionFormat.valueOf(format)));

        throw new UnsupportedOperationException("Unimplemented method 'createConvertion'");
    }
}
