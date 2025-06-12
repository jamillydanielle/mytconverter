package com.mytconvert.usermanagement.repository;

import com.mytconvert.usermanagement.entity.User; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mytconvert.usermanagement.entity.UserType;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findByName(String name);
    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }
    Page<User> findByDeactivatedAtIsNullAndTypeNot(Pageable pageable, UserType type);

}
