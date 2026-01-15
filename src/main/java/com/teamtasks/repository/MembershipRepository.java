package com.teamtasks.repository;

import com.teamtasks.domain.org.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    List<Membership> findAllByUser_Id(UUID userId);

    List<Membership> findAllByOrganization_Id(UUID orgId);

    Optional<Membership> findByOrganization_IdAndUser_Id(UUID orgId, UUID userId);
}