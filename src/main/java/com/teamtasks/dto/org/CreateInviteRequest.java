package com.teamtasks.dto.org;

import com.teamtasks.domain.org.OrgRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateInviteRequest(
        @Email String email,
        @NotNull OrgRole role
) {}