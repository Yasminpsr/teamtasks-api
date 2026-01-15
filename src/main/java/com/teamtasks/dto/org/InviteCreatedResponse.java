package com.teamtasks.dto.org;

import com.teamtasks.domain.org.OrgRole;

import java.time.Instant;
import java.util.UUID;

public record InviteCreatedResponse(
        UUID id,
        String email,
        OrgRole role,
        Instant expiresAt,
        String token,
        String link
) {}