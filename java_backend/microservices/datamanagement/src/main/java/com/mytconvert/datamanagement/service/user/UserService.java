package com.mytconvert.datamanagement.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;
import com.mytconvert.datamanagement.repository.user.UserRepository;
import com.mytconvert.datamanagement.utils.RequestValidator;
import com.mytconvert.security.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserSessionService userSessionService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, UserSessionService userSessionService) {
        this.userRepository = userRepository;
        this.userSessionService = userSessionService;
    }

    public User createUser(User user) {
        validateUserCreation(user);
        
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public User updateUser(Map<String, Object> payload) {

        String userName = (String) payload.get("name");
        String userEmail = (String) payload.get("email");
        String optionalPassWord = (String) payload.get("password");        

        User user = userRepository.findByIdOrThrow(JwtUtils.getCurrentUserId().get());

        if(optionalPassWord != null){
            RequestValidator.validatePasswordStrength(optionalPassWord);
        }

        user.setName(userName);
        user.setEmail(userEmail);
        user.setUpdatedAt(LocalDateTime.now());

        validateUserUpdate(user);
        
        if(optionalPassWord != null){
            String encodedPassword = passwordEncoder.encode(optionalPassWord);
            user.setPassword(encodedPassword);
        }
        

        
        
        return userRepository.save(user);
    }


    private void validateUserCreation(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email é obrigatório");
        }
        
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha é obrigatória");
        }
        
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }
    }

    private void validateUserUpdate(User userDetails) {
        if (userDetails.getName() == null || userDetails.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        
        if (userDetails.getEmail() == null || userDetails.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email é obrigatório");
        }
        
        if (isEmailTakenByAnotherUser(userDetails.getId(), userDetails.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado por outro usuário");
        }
    }

    public boolean isEmailTakenByAnotherUser(Long userId, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent() && !existingUser.get().getId().equals(userId);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmailOrThrow(email);
    }

    public boolean checkUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User deactivateUser(Long id) {
        User user = userRepository.findByIdOrThrow(id);

        if (user.getDeactivatedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario ja esta desativado.");
        }
    
        user.setDeactivatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        
        // Invalidar todas as sessões ativas do usuário
        userSessionService.invalidateAllUserSessions(user);
        
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = userRepository.findByIdOrThrow(id);
    
        if (user.getDeactivatedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario ja esta ativo.");
        }
    
        user.setDeactivatedAt(null);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Método atualizado para incluir usuários desativados
    public Page<User> listUsers(Pageable pageable) {
        // Retorna todos os usuários, exceto administradores
        return userRepository.findByTypeNot(pageable, UserType.ADMIN);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findByIdOrThrow(id);
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getType() == UserType.ADMIN;
    }
    
    public boolean isUser(User user) {
        return user != null && user.getType() == UserType.USER;
    }

    public User createAdminUser(String name, String email, String password) {
        Optional<User> existingAdmin = userRepository.findByEmail(email);
        if (existingAdmin.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um administrador com esse email ja foi cadastrado");
        }
    
        String encodedPassword = passwordEncoder.encode(password);
    
        User adminUser = new User(
            name,
            email,
            encodedPassword,
            UserType.ADMIN
        );
        User createdUser = userRepository.save(adminUser);
        return createdUser;
    }

    public void changePassword(String email, String newPassword) {
        User userOpt = userRepository.findByEmailOrThrow(email);
         
        User user = userOpt;
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }
}