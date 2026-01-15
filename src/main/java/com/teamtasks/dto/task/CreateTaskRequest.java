package com.teamtasks.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank @Size(max = 160) String title,
        String description,
        LocalDate dueDate,
        UUID assignedToUserId
) {}