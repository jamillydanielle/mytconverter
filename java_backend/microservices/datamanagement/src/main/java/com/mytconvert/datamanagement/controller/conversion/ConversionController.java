package com.mytconvert.datamanagement.controller.conversion;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.service.conversion.ConversionService;
import com.mytconvert.datamanagement.utils.RequestValidator;

@RestController
@RequestMapping("/conversions")
public class ConversionController {
    
    private final ConversionService conversionService;

    @Autowired
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/createConversion")
    public ResponseEntity<String> createConversion(@RequestBody Map<String, String> payload) {
        
        List<String> requiredFields = Arrays.asList("internal_file_name", "format");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String internalFileName = payload.get("internal_file_name");
        String format = payload.get("format");

        Conversion createdConversion = conversionService.createConversion(internalFileName, format);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("{\"message\": \"Conversao cadastrada\", \"id\": \"" + createdConversion.getId() + "\"}");
    }
}
