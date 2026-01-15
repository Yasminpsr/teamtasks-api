package com.teamtasks.service.org;

import com.teamtasks.domain.org.*;
import com.teamtasks.domain.user.User;
import com.teamtasks.dto.org.CreateOrgRequest;
import com.teamtasks.repository.MembershipRepository;
import com.teamtasks.repository.OrganizationRepository;
import com.teamtasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public Organization createOrg(UUID userId, CreateOrgRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Organization org = Organization.builder()
                .name(req.name())
                .build();

        org = organizationRepository.save(org);

        Membership owner = Membership.builder()
                .organization(org)
                .user(user)
                .role(OrgRole.OWNER)
                .build();

        membershipRepository.save(owner);

        return org;
    }

    public List<Organization> myOrgs(UUID userId) {
        return membershipRepository.findAllByUser_Id(userId).stream()
                .map(Membership::getOrganization)
                .toList();
    }

    public List<Membership> orgMembers(UUID orgId) {
        return membershipRepository.findAllByOrganization_Id(orgId);
    }

    public void assertMember(UUID orgId, UUID userId) {
        membershipRepository.findByOrganization_IdAndUser_Id(orgId, userId)
                .orElseThrow(() -> new RuntimeException("Sem acesso à organização"));
    }
}