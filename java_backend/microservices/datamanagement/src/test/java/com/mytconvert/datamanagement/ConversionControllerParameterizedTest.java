// package com.mytconvert.datamanagement;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.doReturn;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.mytconvert.datamanagement.controller.conversion.ConversionController;
// import com.mytconvert.datamanagement.entity.conversion.Conversion;
// import com.mytconvert.datamanagement.entity.conversion.ConversionFormat;
// import com.mytconvert.datamanagement.entity.user.User;
// import com.mytconvert.datamanagement.entity.user.UserType;
// import com.mytconvert.datamanagement.service.conversion.ConversionService;
// import com.mytconvert.datamanagement.service.user.UserService;
// import com.mytconvert.datamanagement.utils.RequestValidator;
// import com.mytconvert.datamanagement.utils.ValidationUtils;
// import com.mytconvert.security.utils.JwtUtils;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.CsvSource;
// import org.mockito.MockedStatic;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.FilterType;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// @WebMvcTest(
//     controllers = ConversionController.class,
//     excludeFilters = @ComponentScan.Filter(
//         type = FilterType.ASSIGNABLE_TYPE,
//         classes = com.mytconvert.datamanagement.DataManagementApplication.class
//     )
// )
// @ExtendWith(MockitoExtension.class)
// public class ConversionControllerParameterizedTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockitoBean
//     private ConversionService conversionService;
    
//     @MockitoBean
//     private RequestValidator requestValidator;
    
//     @MockitoBean
//     private UserService userService;

//     private ObjectMapper objectMapper = new ObjectMapper();
    
//     @BeforeEach
//     public void setup() {
//         // Create a test user
//         User testUser = new User(
//             "Test User",
//             "test@example.com",
//             "password",
//             UserType.USER
//         );
//         testUser.setId(1L);
        
//         // Create a mock conversion
//         Conversion mockConversion = new Conversion(
//             testUser,
//             "example.mp4",
//             ConversionFormat.MP4,
//             120L
//         );
        
//         List<Conversion> testConversions = new ArrayList<>();
//         testConversions.add(mockConversion);
        
//         // Mock the conversion service methods
//         when(conversionService.createConversion(any(User.class), anyString(), anyString(), anyLong()))
//             .thenAnswer(invocation -> {
//                 User requester = invocation.getArgument(0);
//                 String fileName = invocation.getArgument(1);
//                 String format = invocation.getArgument(2);
//                 Long length = invocation.getArgument(3);
                
//                 if (length != null && length < 0) {
//                     throw new IllegalArgumentException("Length must be positive");
//                 }
                
//                 if (format == null || format.isEmpty()) {
//                     throw new IllegalArgumentException("Format is required");
//                 }
                
//                 Conversion conversion = new Conversion();
//                 conversion.setId(1L);
//                 conversion.setRequester(requester);
//                 conversion.setInternalFileName(fileName);
//                 try {
//                     conversion.setFormat(ConversionFormat.valueOf(format));
//                 } catch (IllegalArgumentException e) {
//                     throw new IllegalArgumentException("Invalid format");
//                 }
//                 conversion.setLength(length);
//                 return conversion;
//             });
        
//         when(conversionService.getAllConversions()).thenReturn(testConversions);
//         when(conversionService.getConversionsByUserId(anyLong())).thenReturn(testConversions);
        
//         // Mock userService to return the test user
//         when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        
//         // Mock isAdmin and isUser methods
//         when(userService.isAdmin(any(User.class))).thenAnswer(invocation -> {
//             User user = invocation.getArgument(0);
//             return user.getType() == UserType.ADMIN;
//         });
        
//         when(userService.isUser(any(User.class))).thenAnswer(invocation -> {
//             User user = invocation.getArgument(0);
//             return user.getType() == UserType.USER;
//         });
//     }
    
//     /**
//      * Helper method to create a JwtUser for testing
//      * @param id User ID
//      * @param name User name
//      * @param email User email
//      * @param type User type
//      * @return JwtUser instance
//      */
//     private JwtUtils.JwtUser createJwtUser(Long id, String name, String email, String type) {
//         JwtUtils.JwtUser jwtUser = new JwtUtils.JwtUser();
//         jwtUser.setId(id);
//         jwtUser.setName(name);
//         jwtUser.setEmail(email);
//         jwtUser.setType(type);
//         jwtUser.setActive(true);
//         return jwtUser;
//     }
    
//     /**
//      * Helper method to create a User entity for testing
//      * @param id User ID
//      * @param name User name
//      * @param email User email
//      * @param userType User type enum
//      * @return User entity instance
//      */
//     private User createUser(Long id, String name, String email, UserType userType) {
//         User user = new User(name, email, "password", userType);
//         user.setId(id);
//         return user;
//     }

//     @ParameterizedTest
//     @CsvSource({
//         "example.mp4, MP4, 120, 201",
//         "example.mp3, MP3, 180, 201",
//         "example.mp4, MP4, -10, 400",
//         "example.mp4, , 120, 400"
//     })
//     @WithMockUser(roles = "USER")
//     void testCreateConversion(String internalFileName, String format, Long length, int expectedStatus) throws Exception {
//         // Create a User for this specific test
//         Long userId = 1L;
//         User testUser = createUser(userId, "Test User", "test@example.com", UserType.USER);
        
//         // Mock userService to return our test user
//         when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));
        
//         // Create a JwtUser that matches our test user
//         JwtUtils.JwtUser jwtUser = createJwtUser(userId, testUser.getName(), testUser.getEmail(), "USER");
        
//         // Mock static methods
//         try (MockedStatic<JwtUtils> jwtUtilsMock = Mockito.mockStatic(JwtUtils.class);
//              MockedStatic<RequestValidator> validatorMock = Mockito.mockStatic(RequestValidator.class);
//              MockedStatic<ValidationUtils> validationUtilsMock = Mockito.mockStatic(ValidationUtils.class)) {
            
//             // Mock JwtUtils methods
//             jwtUtilsMock.when(JwtUtils::getCurrentUser).thenReturn(Optional.of(jwtUser));
//             jwtUtilsMock.when(JwtUtils::getCurrentUserId).thenReturn(Optional.of(userId));
            
//             // Mock validation methods
//             validatorMock.when(() -> RequestValidator.validateFieldsForMap(any(), any())).thenAnswer(invocation -> null);
            
//             // For negative length or empty format, make validation fail
//             if (length < 0) {
//                 validationUtilsMock.when(() -> ValidationUtils.validatePositiveLong(length, "length"))
//                     .thenThrow(new IllegalArgumentException("Length must be positive"));
//             } else if (format == null || format.isEmpty()) {
//                 validatorMock.when(() -> RequestValidator.validateFieldsForMap(any(), any()))
//                     .thenThrow(new IllegalArgumentException("Format is required"));
//             } else {
//                 validationUtilsMock.when(() -> ValidationUtils.validatePositiveLong(length, "length")).thenAnswer(invocation -> null);
//             }
            
//             String payload = String.format("{ \"internal_file_name\": \"%s\", \"format\": \"%s\", \"length\": %d }", 
//                 internalFileName, format != null ? format : "", length);

//             mockMvc.perform(post("/conversions/createconversion")
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(payload))
//                     .andDo(MockMvcResultHandlers.print())
//                     .andExpect(status().is(expectedStatus));
//         }
//     }

//     @ParameterizedTest
//     @CsvSource({
//         "ADMIN, 200",
//         "USER, 403"
//     })
//     void testListForAdmin(UserType role, int expectedStatus) throws Exception {
//         // Create a User with the appropriate role
//         Long userId = 1L;
//         User roleUser = createUser(userId, "Test User", "test@example.com", role);
        
//         // Mock userService to return our role-specific user
//         when(userService.getUserById(userId)).thenReturn(Optional.of(roleUser));
        
//         // Create a JwtUser that matches our role-specific user
//         JwtUtils.JwtUser jwtUser = createJwtUser(userId, roleUser.getName(), roleUser.getEmail(), role.toString());
        
//         // Mock JwtUtils.getCurrentUser() and getCurrentUserId() for this specific test
//         try (MockedStatic<JwtUtils> jwtUtilsMock = Mockito.mockStatic(JwtUtils.class)) {
//             jwtUtilsMock.when(JwtUtils::getCurrentUser).thenReturn(Optional.of(jwtUser));
//             jwtUtilsMock.when(JwtUtils::getCurrentUserId).thenReturn(Optional.of(userId));
            
//             // Set up security context with the appropriate authority
//             List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//             authorities.add(new SimpleGrantedAuthority(role.toString()));
            
//             SecurityContextHolder.getContext().setAuthentication(
//                 new UsernamePasswordAuthenticationToken(
//                     roleUser.getEmail(), 
//                     null, 
//                     authorities
//                 )
//             );
            
//             mockMvc.perform(get("/conversions/listforadm")
//                     .contentType(MediaType.APPLICATION_JSON))
//                     .andDo(MockMvcResultHandlers.print())
//                     .andExpect(status().is(expectedStatus));
            
//             // Clean up security context
//             SecurityContextHolder.clearContext();
//         }
//     }

//     @ParameterizedTest
//     @CsvSource({
//         "1, USER, 200",
//         "1, ADMIN, 200",
//         "2, USER, 200"
//     })
//     void testListForUser(Long userId, UserType userType, int expectedStatus) throws Exception {
//         // Create a list of conversions to return
//         List<Conversion> userConversions = new ArrayList<>();
        
//         // Create a User with the correct constructor and set the ID separately
//         User user = createUser(userId, "Test User", "test@example.com", userType);
        
//         // Create a conversion associated with this user
//         Conversion conversion = new Conversion();
//         conversion.setId(1L);
//         conversion.setRequester(user);
//         conversion.setInternalFileName("test.mp4");
//         conversion.setFormat(ConversionFormat.MP4);
//         conversion.setLength(120L);
//         userConversions.add(conversion);
        
//         // Mock the service to return these conversions
//         doReturn(userConversions).when(conversionService).getConversionsByUserId(userId);
        
//         // Mock userService to return our user
//         when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        
//         // Create a JwtUser that matches our user
//         JwtUtils.JwtUser jwtUser = createJwtUser(userId, user.getName(), user.getEmail(), userType.toString());
        
//         // Mock JwtUtils.getCurrentUser() and getCurrentUserId() to return a user with the correct ID
//         try (MockedStatic<JwtUtils> jwtUtilsMock = Mockito.mockStatic(JwtUtils.class)) {
//             jwtUtilsMock.when(JwtUtils::getCurrentUser).thenReturn(Optional.of(jwtUser));
//             jwtUtilsMock.when(JwtUtils::getCurrentUserId).thenReturn(Optional.of(userId));
            
//             // Set up security context with the appropriate authority
//             List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//             authorities.add(new SimpleGrantedAuthority(userType.toString()));
            
//             SecurityContextHolder.getContext().setAuthentication(
//                 new UsernamePasswordAuthenticationToken(
//                     user.getEmail(), 
//                     null, 
//                     authorities
//                 )
//             );
            
//             mockMvc.perform(get("/conversions/listforuser")
//                     .contentType(MediaType.APPLICATION_JSON))
//                     .andDo(MockMvcResultHandlers.print())
//                     .andExpect(status().is(expectedStatus));
            
//             // Clean up security context
//             SecurityContextHolder.clearContext();
//         }
//     }
// }