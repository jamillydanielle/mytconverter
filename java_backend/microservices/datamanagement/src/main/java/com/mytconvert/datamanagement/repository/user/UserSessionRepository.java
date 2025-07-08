package com.mytconvert.datamanagement.repository.user;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findFirstByUserOrderByLoginTimeDesc(User user);
    
    // Novo método para buscar todas as sessões ativas de um usuário
    List<UserSession> findByUserAndActiveTrue(User user);
}