package com.mytconvert.usermanagement.service;

import com.mytconvert.usermanagement.entity.User;
import com.mytconvert.usermanagement.entity.UserType;
import com.mytconvert.usermanagement.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        validateUserCreation(user);
        
    
        // Define informações de criação e atualização
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    
        // Codifica a senha
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);       
         
        // Salva o usuário no repositório
        return userRepository.save(user);
    }

    // Método para atualizar um usuário existente
    public User updateUser(Long id, User userDetails) {
        validateUserUpdate(id, userDetails);

        // Busca o usuário existente pelo ID
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));


        // Atualiza os detalhes do usuário
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setType(userDetails.getType());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    private void validateUserCreation(User user) {
        boolean userExists = findByEmail(user.getEmail()).isPresent();
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email already belongs to another user.");
        }
    }

    private void validateUserUpdate(Long id, User userDetails) {
        if (isEmailTakenByAnotherUser(id, userDetails.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email already belongs to another user.");
        }
    }

    // Verifica se o email já pertence a outro usuário
    public boolean isEmailTakenByAnotherUser(Long userId, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent() && !existingUser.get().getId().equals(userId);
    }
    
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean checkUserExists(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            return true;
        }

        return false;
    }

    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already deactivated.");
        }

        user.setDeactivatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    
        if (user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already active.");
        }
    
        user.setDeactivatedAt(null); // Remove a data de desativação
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // List users with pagination
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findByDeactivatedAtIsNullAndTypeNot(pageable, UserType.ADMIN);
    }


    // Retrieve a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Delete a user by ID
    public boolean deleteUser(Long id) {
        // Check if the user exists
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
    
    
    public User createAdminUser(String name, String email, String password) {
        System.out.println("entrou no medo create admin");
        Optional<User> existingAdmin = userRepository.findByEmail(email);
        if (existingAdmin.isPresent()) {
            // Lança uma exceção com status 400 se o email já existir
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An admin user with this email already exists.");
        }
    
        
        String encodedPassword = passwordEncoder.encode(password);
    
        User adminUser = new User(
            name,
            email,
            encodedPassword,
            UserType.ADMIN
        );
        User createdUser = userRepository.save(adminUser);
        System.out.println("Created User: " + createdUser);
        return createdUser;
    }

    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
