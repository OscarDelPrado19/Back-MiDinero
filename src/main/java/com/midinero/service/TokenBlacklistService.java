package com.midinero.service;

import com.midinero.entity.TokenBlacklist;
import com.midinero.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    public void blacklistToken(String token) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    @Scheduled(fixedRate = 3600000) // Ejecutar cada hora
    public void limpiarTokensExpirados() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
        tokenBlacklistRepository.deleteByFechaBloqueoBefore(cutoffDate);
    }
}
