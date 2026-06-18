package com.teamtasks.dto.user;

import java.util.UUID;

public record MeResponse(
        UUID id,
        String name,
        String email,
        String role

) {}