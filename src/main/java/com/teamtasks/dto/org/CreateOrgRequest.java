package com.teamtasks.dto.org;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrgRequest(
        @NotBlank @Size(min = 2, max = 120) String name
) {}