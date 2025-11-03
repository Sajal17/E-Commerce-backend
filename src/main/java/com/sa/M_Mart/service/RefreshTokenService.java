package com.sa.M_Mart.service;

import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(AppUser user);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUser(AppUser user);
    boolean isExpired(RefreshToken token);
}
