package com.mytconvert.convertion.controller;


import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.mapper.ConvertionMapper;
import com.mytconvert.convertion.service.ConvertionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/convert")
public class ConvertionController {

    private final ConvertionService convertionService;
    private final ConvertionMapper mapper;

    @Autowired
    public ConvertionController(ConvertionService convertionService, ConvertionMapper mapper) {
        this.convertionService = convertionService;
        this.mapper = mapper;
    }

    @PostMapping("/createConvertion")
    public ResponseEntity<String> createConvertion(@RequestBody Map<String, Object> payload) {
        
        Convertion create = mapper.mapPayloadToConvertion(payload);

        convertionService.createConvertion(create);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("{\"message\": \"New convertion added\"}");
    }

}
