package com.mytconvert.datamanagement.controller.conversion;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.service.conversion.ConversionService;
import com.mytconvert.datamanagement.utils.RequestValidator;
import com.mytconvert.datamanagement.utils.ValidationUtils;
import com.mytconvert.security.utils.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mytconvert.datamanagement.service.user.UserService;

@RestController
@RequestMapping("/conversions")
public class ConversionController {
    
    private final ConversionService conversionService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ConversionController.class);

    @Autowired
    public ConversionController(ConversionService conversionService, UserService userService) {
        this.conversionService = conversionService;
        this.userService = userService;
    }

    @PostMapping("/createconversion")
    public ResponseEntity<String> createConversion(@RequestBody Map<String, Object> payload) {
        
        // Updated required fields list to include youtube_video_name and youtube_url
        List<String> requiredFields = List.of("internal_file_name", "format", "length", "youtube_video_name", "youtube_url");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String internalFileName = (String) payload.get("internal_file_name");
        String format = (String) payload.get("format");
        Long length = Long.valueOf(payload.get("length").toString());
        String youtubeVideoName = (String) payload.get("youtube_video_name");
        String youtubeUrl = (String) payload.get("youtube_url");

        // Validate length
        ValidationUtils.validatePositiveLong(length, "length");

        
        // Create conversion with all required fields
        Conversion createdConversion = conversionService.createConversion(
            currentUserEntity(), 
            internalFileName, 
            youtubeVideoName, 
            youtubeUrl, 
            format, 
            length
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("{\"message:"  + "Nova conversao cadastrada\"}");
    }

    @GetMapping("/listforadm")
    public ResponseEntity<?> listForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Fixed the logic error - now checking if user is NOT an admin
        if(!userService.isAdmin(currentUserEntity())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Acesso negado!\"}");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Conversion> conversions = conversionService.getAllConversionsPaginated(pageable);
        return ResponseEntity.ok(conversions);
    }

    @GetMapping("/listforuser")
    public ResponseEntity<?> listForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if(userService.isAdmin(currentUserEntity()) || userService.isUser(currentUserEntity())) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Conversion> conversions = conversionService.getConversionsByUserIdPaginated(currentUserEntity().getId(), pageable);
            return ResponseEntity.ok(conversions);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Acesso negado!\"}");
        }
    }

    private User currentUserEntity(){
        return userService.getUserById(JwtUtils.getCurrentUserId().get()).get();
    }
}