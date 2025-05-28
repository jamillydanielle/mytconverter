package com.mytconvert.convertion.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.mapper.ConvertionMapper;
import com.mytconvert.convertion.service.ConvertionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
