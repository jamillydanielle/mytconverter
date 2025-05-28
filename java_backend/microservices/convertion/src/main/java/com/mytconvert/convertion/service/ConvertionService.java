package com.mytconvert.convertion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import com.mytconvert.convertion.entity.Convertion;
import com.mytconvert.convertion.mapper.ConvertionMapper;
import com.mytconvert.convertion.repository.ConvertionRepository;
import com.mytconvert.security.utils.JwtUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConvertionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ConvertionRepository convertionRepository;
    private final ConvertionMapper convertionMapper;

    @Autowired
    public ConvertionService(ConvertionRepository convertionRepository, ConvertionMapper convertionMapper, RestTemplate restTemplate) {
        this.convertionRepository = convertionRepository;
        this.convertionMapper = convertionMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public Convertion createConvertion(Convertion convertion) {
        
        return convertionRepository.save(convertion);
    }
}