package com.sa.M_Mart.service;

import com.sa.M_Mart.exception.TokenExpiredException;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.RefreshToken;
import com.sa.M_Mart.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiry-seconds:2592000}") // default 30 days
    private long refreshTokenExpirySeconds;

    @Override
    public RefreshToken createRefreshToken(AppUser user) {
        if(user == null) throw new IllegalArgumentException("User cannot be null");

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpirySeconds));
        token.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(token);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(isExpired(token)){
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public int deleteByUser(AppUser user) {
        if(user == null) return 0;
        return refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public boolean isExpired(RefreshToken refreshToken) {
        if(refreshToken == null || refreshToken.getExpiryDate() == null){
            throw new IllegalArgumentException("Refresh token or expiry date is null");
        }
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }
}
