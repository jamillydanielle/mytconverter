package com.mytconvert.convertion.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.convertion.dto.ConvertionDTO;
import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.mapper.ConvertionMapper;
import com.mytconvert.convertion.service.ConvertionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/convertions")
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

    @GetMapping("/getConvertions")
    public ResponseEntity<Page<ConvertionDTO>> getConvertions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userName") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Convertion> convertionPage = convertionService.getConvertions(pageable);

        List<ConvertionDTO> convertionDataList = convertionPage.getContent().stream()
                .map(mapper::mapEntitytoDTO)
                .collect(Collectors.toList());

        Page<ConvertionDTO> convertionDataPage = new PageImpl<>(convertionDataList, pageable, convertionPage.getTotalElements());

        return ResponseEntity.ok(convertionDataPage);
    }


}
