package com.teamtasks.dto.org;

import com.teamtasks.domain.org.OrgRole;

import java.util.UUID;

public record MemberResponse(
        UUID userId,
        String name,
        String email,
        OrgRole role
) {}