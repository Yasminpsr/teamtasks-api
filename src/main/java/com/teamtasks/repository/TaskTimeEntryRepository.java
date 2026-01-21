package com.teamtasks.repository;

import com.teamtasks.domain.task.TaskTimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TaskTimeEntryRepository extends JpaRepository<TaskTimeEntry, UUID> {

    Optional<TaskTimeEntry> findFirstByTask_IdAndEndedAtIsNull(UUID taskId);

    @Query(value = """
        SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (ended_at - started_at))), 0)
        FROM task_time_entries
        WHERE task_id = :taskId
          AND ended_at IS NOT NULL
        """, nativeQuery = true)
    long sumTrackedSeconds(@Param("taskId") UUID taskId);
}