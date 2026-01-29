package com.teamtasks.service.task;

import com.teamtasks.domain.task.Task;
import com.teamtasks.domain.task.TaskTimeEntry;
import com.teamtasks.domain.user.User;
import com.teamtasks.dto.task.TaskTimeSummaryResponse;
import com.teamtasks.exception.BadRequestException;
import com.teamtasks.exception.ForbiddenException;
import com.teamtasks.exception.NotFoundException;
import com.teamtasks.repository.MembershipRepository;
import com.teamtasks.repository.TaskRepository;
import com.teamtasks.repository.TaskTimeEntryRepository;
import com.teamtasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskTimerService {

    private final TaskRepository taskRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final TaskTimeEntryRepository timeEntryRepository;

    private Task getTaskOrThrow(UUID orgId, UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task não encontrada"));

        if (!task.getOrganization().getId().equals(orgId)) {
            throw new ForbiddenException("Task não pertence à org");
        }

        return task;
    }

    private void requireMember(UUID orgId, UUID userId) {
        membershipRepository.findByOrganization_IdAndUser_Id(orgId, userId)
                .orElseThrow(() -> new ForbiddenException("Sem acesso à organização"));
    }

    private void requireAssignee(Task task, UUID actorId) {
        if (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(actorId)) {
            throw new ForbiddenException("Apenas o responsável da tarefa pode usar o timer");
        }
    }

    public void start(UUID orgId, UUID taskId, UUID actorId) {
        requireMember(orgId, actorId);
        Task task = getTaskOrThrow(orgId, taskId);
        requireAssignee(task, actorId);

        timeEntryRepository.findFirstByTask_IdAndEndedAtIsNull(taskId)
                .ifPresent(e -> { throw new BadRequestException("Timer já está rodando"); });

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        TaskTimeEntry entry = TaskTimeEntry.builder()
                .task(task)
                .startedBy(actor)
                .startedAt(Instant.now())
                .build();

        timeEntryRepository.save(entry);
    }

    public void pause(UUID orgId, UUID taskId, UUID actorId) {
        requireMember(orgId, actorId);
        Task task = getTaskOrThrow(orgId, taskId);
        requireAssignee(task, actorId);

        TaskTimeEntry running = timeEntryRepository.findFirstByTask_IdAndEndedAtIsNull(taskId)
                .orElseThrow(() -> new BadRequestException("Nenhum timer rodando para pausar"));

        running.setEndedAt(Instant.now());
        timeEntryRepository.save(running);
    }

    public void stop(UUID orgId, UUID taskId, UUID actorId) {
        pause(orgId, taskId, actorId);
    }

    public TaskTimeSummaryResponse summary(UUID orgId, UUID taskId, UUID actorId) {
        requireMember(orgId, actorId);
        Task task = getTaskOrThrow(orgId, taskId);

        boolean running = timeEntryRepository.findFirstByTask_IdAndEndedAtIsNull(taskId).isPresent();
        long totalSeconds = timeEntryRepository.sumTrackedSeconds(taskId);

        return new TaskTimeSummaryResponse(
                task.getId(),
                totalSeconds,
                totalSeconds / 60,
                totalSeconds / 3600.0,
                running
        );
    }
}