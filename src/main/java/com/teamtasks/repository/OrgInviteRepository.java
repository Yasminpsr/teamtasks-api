package com.teamtasks.repository;

import com.teamtasks.domain.org.OrgInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrgInviteRepository extends JpaRepository<OrgInvite, UUID> {
    Optional<OrgInvite> findByTokenHash(String tokenHash);
    List<OrgInvite> findAllByOrganization_Id(UUID orgId);
}