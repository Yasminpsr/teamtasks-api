package com.teamtasks.controller.task;

import com.teamtasks.dto.task.TaskTimeSummaryResponse;
import com.teamtasks.service.task.TaskTimerService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.teamtasks.dto.task.TaskTimeDailySummaryResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs/{orgId}/tasks/{taskId}/timer")
public class TaskTimerController {

    private final TaskTimerService timerService;

    public TaskTimerController(TaskTimerService timerService) {
        this.timerService = timerService;
    }

    @PostMapping("/start")
    public void start(@PathVariable UUID orgId, @PathVariable UUID taskId, Authentication auth) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        timerService.start(orgId, taskId, actorId);
    }

    @PostMapping("/pause")
    public void pause(@PathVariable UUID orgId, @PathVariable UUID taskId, Authentication auth) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        timerService.pause(orgId, taskId, actorId);
    }

    @PostMapping("/stop")
    public void stop(@PathVariable UUID orgId, @PathVariable UUID taskId, Authentication auth) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        timerService.stop(orgId, taskId, actorId);
    }

    @GetMapping("/summary")
    public TaskTimeSummaryResponse summary(
            @PathVariable UUID orgId,
            @PathVariable UUID taskId,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        return timerService.summary(orgId, taskId, actorId);
    }

    @GetMapping("/summary/daily")
    public List<TaskTimeDailySummaryResponse> dailySummary(
            @PathVariable UUID orgId,
            @PathVariable UUID taskId,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        return timerService.dailySummary(orgId, taskId, actorId);
    }
}