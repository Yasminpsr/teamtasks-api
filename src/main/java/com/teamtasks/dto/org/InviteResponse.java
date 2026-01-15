package com.teamtasks.dto.org;

import com.teamtasks.domain.org.OrgRole;

import java.time.Instant;
import java.util.UUID;

public record InviteResponse(
        UUID id,
        String email,
        OrgRole role,
        Instant expiresAt,
        Instant acceptedAt,
        Instant revokedAt
) {}