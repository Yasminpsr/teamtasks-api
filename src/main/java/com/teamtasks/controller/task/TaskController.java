package com.teamtasks.controller.task;

import com.teamtasks.domain.task.TaskStatus;
import com.teamtasks.dto.task.*;
import com.teamtasks.service.task.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs/{orgId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse create(@PathVariable UUID orgId, @RequestBody @Valid CreateTaskRequest req, Authentication auth) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        var task = taskService.create(orgId, actorId, req);
        return toResponse(task);
    }

    @GetMapping
    public List<TaskResponse> list(
            @PathVariable UUID orgId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        return taskService.list(orgId, actorId, status, start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateStatus(
            @PathVariable UUID orgId,
            @PathVariable UUID taskId,
            @RequestBody @Valid UpdateTaskStatusRequest req,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        var task = taskService.updateStatus(orgId, taskId, actorId, req.status());
        return toResponse(task);
    }

    @PatchMapping("/{taskId}/assign")
    public TaskResponse reassign(
            @PathVariable UUID orgId,
            @PathVariable UUID taskId,
            @RequestParam UUID userId,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        var task = taskService.reassign(orgId, taskId, actorId, userId);
        return toResponse(task);
    }

    private TaskResponse toResponse(com.teamtasks.domain.task.Task task) {
        boolean overdue = task.getDueDate() != null
                && task.getStatus() != TaskStatus.DONE
                && task.getDueDate().isBefore(LocalDate.now());

        return new TaskResponse(
                task.getId(),
                task.getOrganization().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                overdue,
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getEmail() : null,
                task.getCreatedBy().getId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}