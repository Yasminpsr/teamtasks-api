package com.teamtasks.dto.org;

import jakarta.validation.constraints.NotBlank;

public record AcceptInviteRequest(
        @NotBlank String token
) {}