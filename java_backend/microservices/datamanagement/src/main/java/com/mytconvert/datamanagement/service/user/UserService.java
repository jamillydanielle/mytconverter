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

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        validateUserCreation(user);
        
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        validateUserUpdate(id, userDetails);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        
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

    private void validateUserUpdate(Long id, User userDetails) {
        if (userDetails.getName() == null || userDetails.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        
        if (userDetails.getEmail() == null || userDetails.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email é obrigatório");
        }
        
        if (isEmailTakenByAnotherUser(id, userDetails.getEmail())) {
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

    public boolean checkUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    
        if (user.getDeactivatedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario ja esta desativado.");
        }
    
        user.setDeactivatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    
        if (user.getDeactivatedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario ja esta ativo.");
        }
    
        user.setDeactivatedAt(null);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findByDeactivatedAtIsNullAndTypeNot(pageable, UserType.ADMIN);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
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
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
        
        User user = userOpt.get();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }
}