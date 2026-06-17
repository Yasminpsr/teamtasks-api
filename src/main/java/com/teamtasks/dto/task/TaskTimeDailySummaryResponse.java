package com.teamtasks.dto.task;

import java.time.LocalDate;

public record TaskTimeDailySummaryResponse(
        LocalDate date,
        long totalSeconds,
        long totalMinutes,
        double totalHours
) {
}