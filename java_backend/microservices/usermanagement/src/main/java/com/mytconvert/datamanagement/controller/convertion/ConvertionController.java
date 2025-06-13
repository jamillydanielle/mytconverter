package com.mytconvert.datamanagement.controller.convertion;

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

import com.mytconvert.datamanagement.entity.convertion.Convertion;
import com.mytconvert.datamanagement.service.convertion.ConvertionService;
import com.mytconvert.datamanagement.utils.RequestValidator;

@RestController
@RequestMapping("/convertions")
public class ConvertionController {
    
    private final ConvertionService convertionService;

    @Autowired
    public ConvertionController(ConvertionService convertionService) {
        this.convertionService = convertionService;
    }

    @PostMapping("/createConvertion")
    public ResponseEntity<String> createConvertion(@RequestBody Map<String, String> payload) {
        
        List<String> requiredFields = Arrays.asList("internal_file_name", "format");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String internalFileName = payload.get("internal_file_name");
        String format = payload.get("format");

        Convertion createdConvertion = convertionService.createConvertion(internalFileName, format);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("{\"message\": \"Conversao cadastrada\", \"id\": \"" + createdConvertion.getId() + "\"}");
    }

    @GetMapping("/checkUser")
    public ResponseEntity<?> checkUser(@RequestParam String username) {
        boolean userExists = convertionService.findByEmail(username).isPresent();
        return ResponseEntity.ok().body(userExists);
    }

}
