package com.teamtasks.service.task;

import com.teamtasks.domain.org.Membership;
import com.teamtasks.domain.org.OrgRole;
import com.teamtasks.domain.task.Task;
import com.teamtasks.domain.task.TaskStatus;
import com.teamtasks.domain.user.User;
import com.teamtasks.dto.task.CreateTaskRequest;
import com.teamtasks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    private Membership requireMember(UUID orgId, UUID userId) {
        return membershipRepository.findByOrganization_IdAndUser_Id(orgId, userId)
                .orElseThrow(() -> new RuntimeException("Sem acesso à organização"));
    }

    public Task create(UUID orgId, UUID actorId, CreateTaskRequest req) {
        requireMember(orgId, actorId);

        var org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Org não encontrada"));

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        User assignee = null;
        if (req.assignedToUserId() != null) {
            // só permite atribuir para alguém da org
            membershipRepository.findByOrganization_IdAndUser_Id(orgId, req.assignedToUserId())
                    .orElseThrow(() -> new RuntimeException("Assignee não pertence à org"));

            assignee = userRepository.findById(req.assignedToUserId())
                    .orElseThrow(() -> new RuntimeException("Assignee não encontrado"));
        }

        Task task = Task.builder()
                .organization(org)
                .title(req.title())
                .description(req.description())
                .status(TaskStatus.TODO)
                .dueDate(req.dueDate())
                .assignedTo(assignee)
                .createdBy(actor)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> list(UUID orgId, UUID actorId, TaskStatus status, LocalDate start, LocalDate end) {
        requireMember(orgId, actorId);

        if (status != null) return taskRepository.findAllByOrganization_IdAndStatus(orgId, status);
        if (start != null && end != null) return taskRepository.findAllByOrganization_IdAndDueDateBetween(orgId, start, end);

        return taskRepository.findAllByOrganization_Id(orgId);
    }

    public Task updateStatus(UUID orgId, UUID taskId, UUID actorId, TaskStatus status) {
        requireMember(orgId, actorId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada"));

        if (!task.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Task não pertence à org");
        }

        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task reassign(UUID orgId, UUID taskId, UUID actorId, UUID newAssigneeUserId) {
        Membership actor = requireMember(orgId, actorId);

        if (actor.getRole() == OrgRole.MEMBER) {
            throw new RuntimeException("Sem permissão para reatribuir (apenas ADMIN/OWNER)");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada"));

        if (!task.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Task não pertence à org");
        }

        membershipRepository.findByOrganization_IdAndUser_Id(orgId, newAssigneeUserId)
                .orElseThrow(() -> new RuntimeException("Novo assignee não pertence à org"));

        User newAssignee = userRepository.findById(newAssigneeUserId)
                .orElseThrow(() -> new RuntimeException("Assignee não encontrado"));

        task.setAssignedTo(newAssignee);
        return taskRepository.save(task);
    }
}