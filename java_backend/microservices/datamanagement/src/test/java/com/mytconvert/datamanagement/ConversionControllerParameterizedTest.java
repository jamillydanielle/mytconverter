package com.mytconvert.datamanagement;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytconvert.datamanagement.controller.conversion.ConversionController;
import com.mytconvert.datamanagement.entity.conversion.Conversion;
import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;
import com.mytconvert.datamanagement.service.conversion.ConversionService;
import com.mytconvert.datamanagement.service.user.UserService;
import com.mytconvert.datamanagement.utils.RequestValidator;
import com.mytconvert.security.utils.JwtUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(
    controllers = ConversionController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = com.mytconvert.datamanagement.DataManagementApplication.class
    )
)
@ExtendWith(MockitoExtension.class)
public class ConversionControllerParameterizedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionService conversionService;
    
    @MockBean
    private RequestValidator requestValidator;
    
    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    public void setup() {
        // Create a test user
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setType(UserType.USER);
        
        // Create a mock conversion
        Conversion mockConversion = new Conversion();
        mockConversion.setId(1L);
        mockConversion.setRequester(testUser);
        mockConversion.setInternalFileName("example.mp4");
        mockConversion.setFormat(ConversionFormat.MP4);
        mockConversion.setLength(120L);
        
        List<Conversion> testConversions = new ArrayList<>();
        testConversions.add(mockConversion);
        
        // Mock the conversion service methods
        when(conversionService.createConversion(anyString(), anyString(), anyLong()))
            .thenAnswer(invocation -> {
                String fileName = invocation.getArgument(0);
                String format = invocation.getArgument(1);
                Long length = invocation.getArgument(2);
                
                if (length != null && length < 0) {
                    throw new IllegalArgumentException("Length must be positive");
                }
                
                if (format == null || format.isEmpty()) {
                    throw new IllegalArgumentException("Format is required");
                }
                
                Conversion conversion = new Conversion();
                conversion.setId(1L);
                conversion.setRequester(testUser);
                conversion.setInternalFileName(fileName);
                try {
                    conversion.setFormat(ConversionFormat.valueOf(format));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid format");
                }
                conversion.setLength(length);
                return conversion;
            });
        
        when(conversionService.getAllConversions()).thenReturn(testConversions);
        when(conversionService.getConversionsByUserId(anyLong())).thenReturn(testConversions);
    }

    @ParameterizedTest
    @CsvSource({
        "example.mp4, MP4, 120, 201",
        "example.mp3, MP3, 180, 201",
        "example.mp4, MP4, -10, 400",
        "example.mp4, , 120, 400"
    })
    @WithMockUser(roles = "USER")
    void testCreateConversion(String internalFileName, String format, Long length, int expectedStatus) throws Exception {
        // Set up the test user for this specific test
        JwtUtils.User currentUser = new JwtUtils.User(
            1L,
            "Test User",
            "test@example.com",
            "USER",
            true
        );
        
        // Mock JwtUtils.getCurrentUser() for this specific test
        try (MockedStatic<JwtUtils> jwtUtilsMock = Mockito.mockStatic(JwtUtils.class)) {
            jwtUtilsMock.when(JwtUtils::getCurrentUser).thenReturn(Optional.of(currentUser));
            
            String payload = String.format("{ \"internal_file_name\": \"%s\", \"format\": \"%s\", \"length\": %d }", 
                internalFileName, format != null ? format : "", length);

            mockMvc.perform(post("/conversions/createConversion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(expectedStatus));
        }
    }

    @ParameterizedTest
    @CsvSource({
        "ADMIN, 200",
        "USER, 403"
    })
    void testListForAdmin(String role, int expectedStatus) throws Exception {
        // Create a user with the appropriate role
        JwtUtils.User roleUser = new JwtUtils.User(
            1L,
            "Test User",
            "test@example.com",
            role,
            true
        );
        
        // Mock JwtUtils.getCurrentUser() for this specific test
        try (MockedStatic<JwtUtils> jwtUtilsMock = Mockito.mockStatic(JwtUtils.class)) {
            jwtUtilsMock.when(JwtUtils::getCurrentUser).thenReturn(Optional.of(roleUser));
            
            // Set up security context with the appropriate authority
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    "test@example.com", 
                    null, 
                    authorities
                )
            );
            
            mockMvc.perform(get("/conversions/listforadm")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(expectedStatus));
            
            // Clean up security context
            SecurityContextHolder.clearContext();
        }
    }

    @ParameterizedTest
    @CsvSource({
        "1, 200",
        "2, 200"
    })
    @WithMockUser
    void testListForUser(Long userId, int expectedStatus) throws Exception {
        // Create a list of conversions to return
        List<Conversion> userConversions = new ArrayList<>();
        User user = new User();
        user.setId(userId);
        
        Conversion conversion = new Conversion();
        conversion.setId(1L);
        conversion.setRequester(user);
        conversion.setInternalFileName("test.mp4");
        conversion.setFormat(ConversionFormat.MP4);
        conversion.setLength(120L);
        userConversions.add(conversion);
        
        // Mock the service to return these conversions
        doReturn(userConversions).when(conversionService).getConversionsByUserId(userId);
        
        // Mock JwtUtils.getCurrentUser() to return a user with the correct ID
        try (MockedStatic<JwtUtils> jwtUtilsMock = Mockito.mockStatic(JwtUtils.class)) {
            // Create a mock JwtUtils.User that matches what the controller expects
            JwtUtils.User jwtUser = new JwtUtils.User(
                userId,
                "Test User",
                "test@example.com",
                "USER",
                true
            );
            
            jwtUtilsMock.when(JwtUtils::getCurrentUser).thenReturn(Optional.of(jwtUser));
            
            mockMvc.perform(get("/conversions/listforuser")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(expectedStatus));
        }
    }
}