package com.teamtasks.repository;

import com.teamtasks.domain.task.Task;
import com.teamtasks.domain.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByOrganization_Id(UUID orgId);

    List<Task> findAllByOrganization_IdAndStatus(UUID orgId, TaskStatus status);

    List<Task> findAllByOrganization_IdAndDueDateBetween(UUID orgId, LocalDate start, LocalDate end);
}