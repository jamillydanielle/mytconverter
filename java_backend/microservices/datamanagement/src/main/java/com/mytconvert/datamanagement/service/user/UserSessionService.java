package com.mytconvert.datamanagement.service.user;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserSession;
import com.mytconvert.datamanagement.repository.user.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Autowired
    public UserSessionService(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    public UserSession createSession(User user) {
        UserSession session = new UserSession(user);
        session.setActive(true);
        return userSessionRepository.save(session);
    }

    public LocalDateTime getLastSessionTime(User user) {
        Optional<UserSession> lastSession = userSessionRepository.findFirstByUserOrderByLoginTimeDesc(user);
        return lastSession.map(UserSession::getLoginTime).orElse(null);
    }
    
    /**
     * Invalida todas as sessões ativas de um usuário
     * @param user O usuário cujas sessões devem ser invalidadas
     * @return O número de sessões invalidadas
     */
    public int invalidateAllUserSessions(User user) {
        List<UserSession> activeSessions = userSessionRepository.findByUserAndActiveTrue(user);
        
        if (activeSessions.isEmpty()) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        for (UserSession session : activeSessions) {
            session.setActive(false);
            session.setLogoutTime(now);
        }
        
        userSessionRepository.saveAll(activeSessions);
        return activeSessions.size();
    }
}