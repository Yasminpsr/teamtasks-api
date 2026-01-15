package com.teamtasks.service.auth;

import com.teamtasks.config.security.JwtService;
import com.teamtasks.domain.user.RefreshToken;
import com.teamtasks.domain.user.User;
import com.teamtasks.dto.auth.AuthTokens;
import com.teamtasks.dto.auth.LoginRequest;
import com.teamtasks.repository.RefreshTokenRepository;
import com.teamtasks.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthTokens login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        String access = jwtService.generateAccessToken(
                user.getId().toString(),
                user.getEmail()
        );

        String refresh = jwtService.generateRefreshToken(user.getId().toString());
        saveRefresh(user, refresh);

        return new AuthTokens(access, refresh);
    }

    public AuthTokens refresh(String refreshRaw) {

        Jws<Claims> parsed = jwtService.parse(refreshRaw);
        if (!"refresh".equals(parsed.getBody().get("type", String.class))) {
            throw new IllegalArgumentException("Token inválido");
        }

        String hash = TokenHash.sha256(refreshRaw);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh inválido"));

        if (stored.isRevoked() || stored.isExpired()) {
            throw new IllegalArgumentException("Refresh expirado ou revogado");
        }

        User user = stored.getUser();

        // revoga o antigo
        stored.setRevokedAt(Instant.now());
        refreshTokenRepository.save(stored);

        // gera novos
        String newRefresh = jwtService.generateRefreshToken(user.getId().toString());
        saveRefresh(user, newRefresh);

        String access = jwtService.generateAccessToken(
                user.getId().toString(),
                user.getEmail()
        );

        return new AuthTokens(access, newRefresh);
    }

    public void logout(String refreshRaw) {
        String hash = TokenHash.sha256(refreshRaw);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            rt.setRevokedAt(Instant.now());
            refreshTokenRepository.save(rt);
        });
    }

    private void saveRefresh(User user, String refreshRaw) {
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .tokenHash(TokenHash.sha256(refreshRaw))
                .expiresAt(jwtService.refreshExpiresAtFromNow())
                .build();

        refreshTokenRepository.save(rt);
    }
}