package com.teamtasks.repository;

import com.teamtasks.domain.task.TaskTimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query(value = """
    select
        cast(started_at as date) as date,
        coalesce(sum(extract(epoch from (ended_at - started_at))), 0) as total_seconds
    from task_time_entries
    where task_id = :taskId
      and ended_at is not null
    group by cast(started_at as date)
    order by cast(started_at as date) desc
    """, nativeQuery = true)
    List<Object[]> sumTrackedSecondsByDay(@Param("taskId") UUID taskId);

}