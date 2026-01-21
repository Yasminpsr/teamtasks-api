package com.teamtasks.domain.task;

import com.teamtasks.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task_time_entries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TaskTimeEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(optional = false)
    @JoinColumn(name = "started_by")
    private User startedBy;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (startedAt == null) startedAt = Instant.now();
    }
}