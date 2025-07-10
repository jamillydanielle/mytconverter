package com.mytconvert.datamanagement.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    default User findByEmailOrThrow(String email){
        return findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }

    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }
    Page<User> findByDeactivatedAtIsNullAndTypeNot(Pageable pageable, UserType type);
    
    // Novo método para buscar todos os usuários, incluindo desativados
    Page<User> findByTypeNot(Pageable pageable, UserType type);
}