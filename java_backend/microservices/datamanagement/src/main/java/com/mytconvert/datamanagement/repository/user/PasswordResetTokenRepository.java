package com.mytconvert.datamanagement.repository.user;

import com.mytconvert.datamanagement.entity.user.PasswordResetToken;
import com.mytconvert.datamanagement.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
}