package com.teamtasks.dto.auth;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}