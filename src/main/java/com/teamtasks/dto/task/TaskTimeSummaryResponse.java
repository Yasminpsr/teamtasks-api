package com.teamtasks.dto.task;

import java.util.UUID;

public record TaskTimeSummaryResponse(
        UUID taskId,
        long totalSeconds,
        long totalMinutes,
        double totalHours,
        boolean running
) {}