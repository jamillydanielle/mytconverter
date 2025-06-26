package com.mytconvert.datamanagement.controller.conversion;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.service.conversion.ConversionService;
import com.mytconvert.datamanagement.utils.RequestValidator;
import com.mytconvert.datamanagement.utils.ValidationUtils;
import com.mytconvert.security.utils.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/conversions")
public class ConversionController {
    
    private final ConversionService conversionService;
    private static final Logger logger = LoggerFactory.getLogger(ConversionController.class);

    @Autowired
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/createConversion")
    public ResponseEntity<String> createConversion(@RequestBody Map<String, Object> payload) {
        
        List<String> requiredFields = List.of("internal_file_name", "format", "length");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String internalFileName = (String) payload.get("internal_file_name");
        String format = (String) payload.get("format");
        Long length = Long.valueOf(payload.get("length").toString());

        // Validate length
        ValidationUtils.validatePositiveLong(length, "length");

        logger.info("Creating conversion with internalFileName: {}, format: {}, length: {}", internalFileName, format, length);

        Conversion createdConversion = conversionService.createConversion(internalFileName, format, length);

        logger.info("Conversion created successfully with ID: {}", createdConversion.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("{\"message:"  + "Nova conversao cadastrada\", \"id\": \"" + createdConversion.getId() + "\"}");
    }

    @GetMapping("/listforadm")
    public ResponseEntity<?> listForAdmin() {
        if(!JwtUtils.getCurrentUser().get().getType().equals("ADMIN")) {
            logger.error("Access denied: User is not an admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Access denied: User is not an admin\"}");
        }
        List<Conversion> conversions = conversionService.getAllConversions();
        return ResponseEntity.ok(conversions);
    }

    @GetMapping("/listforuser")
    public ResponseEntity<?> listForUser() {
        if(JwtUtils.getCurrentUser().get().getType().equals("ADMIN") || JwtUtils.getCurrentUser().get().getType().equals("USER")) {
            Long userId = JwtUtils.getCurrentUser().get().getId();
            List<Conversion> conversions = conversionService.getConversionsByUserId(userId);
            return ResponseEntity.ok(conversions);
        } else {
            logger.error("Access denied: User is not authorized");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Access denied: User is not authorized\"}");
        }
    }
}