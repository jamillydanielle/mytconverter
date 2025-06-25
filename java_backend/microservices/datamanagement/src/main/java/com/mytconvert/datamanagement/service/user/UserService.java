package com.mytconvert.datamanagement.service.user;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;
import com.mytconvert.datamanagement.repository.user.UserRepository;

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

    @Autowired
    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        validateUserCreation(user);
        
    
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);       
         
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        validateUserUpdate(id, userDetails);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));


        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setType(userDetails.getType());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    private void validateUserCreation(User user) {
        boolean userExists = findByEmail(user.getEmail()).isPresent();
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este email pertence a outro usuario");
        }
    }

    private void validateUserUpdate(Long id, User userDetails) {
        if (isEmailTakenByAnotherUser(id, userDetails.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este email pertence a outro usuario");
        }
    }

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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario ja foi desativado");
        }

        user.setDeactivatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    
        if (user.isActive()) {
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
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Nao ha nenhum usuario com o email: " + email));
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
