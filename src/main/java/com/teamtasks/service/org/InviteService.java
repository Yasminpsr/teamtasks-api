package com.teamtasks.service.org;

import com.teamtasks.domain.org.*;
import com.teamtasks.domain.user.User;
import com.teamtasks.dto.org.CreateInviteRequest;
import com.teamtasks.repository.*;
import com.teamtasks.service.auth.TokenHash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final OrgInviteRepository inviteRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    private static final SecureRandom RNG = new SecureRandom();

    public record CreatedInvite(OrgInvite invite, String rawToken) {}

    public CreatedInvite createInvite(UUID orgId, UUID actorId, CreateInviteRequest req) {
        Membership actorMembership = membershipRepository
                .findByOrganization_IdAndUser_Id(orgId, actorId)
                .orElseThrow(() -> new RuntimeException("Sem acesso"));

        if (actorMembership.getRole() == OrgRole.MEMBER) {
            throw new RuntimeException("Sem permissão (apenas ADMIN/OWNER)");
        }

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Org não encontrada"));

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String rawToken = generateToken();
        String hash = TokenHash.sha256(rawToken);

        OrgInvite invite = OrgInvite.builder()
                .organization(org)
                .email(req.email().toLowerCase())
                .role(req.role())
                .tokenHash(hash)
                .expiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .createdBy(actor)
                .build();

        inviteRepository.save(invite);

        return new CreatedInvite(invite, rawToken);
    }

    public void updateInviteRole(UUID orgId, UUID inviteId, UUID actorId, OrgRole newRole) {
        Membership actorMembership = membershipRepository
                .findByOrganization_IdAndUser_Id(orgId, actorId)
                .orElseThrow(() -> new RuntimeException("Sem acesso"));

        if (actorMembership.getRole() == OrgRole.MEMBER) {
            throw new RuntimeException("Sem permissão (apenas ADMIN/OWNER)");
        }

        OrgInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        if (!invite.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Convite não pertence à org");
        }

        if (invite.getAcceptedAt() != null || invite.getRevokedAt() != null) {
            throw new RuntimeException("Convite não pode ser alterado");
        }

        invite.setRole(newRole);
        inviteRepository.save(invite);
    }

    public void revokeInvite(UUID orgId, UUID inviteId, UUID actorId) {
        Membership actorMembership = membershipRepository
                .findByOrganization_IdAndUser_Id(orgId, actorId)
                .orElseThrow(() -> new RuntimeException("Sem acesso"));

        if (actorMembership.getRole() == OrgRole.MEMBER) {
            throw new RuntimeException("Sem permissão (apenas ADMIN/OWNER)");
        }

        OrgInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        if (!invite.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Convite não pertence à org");
        }

        invite.setRevokedAt(Instant.now());
        inviteRepository.save(invite);
    }

    public Membership acceptInvite(UUID actorId, String rawToken) {
        String hash = TokenHash.sha256(rawToken);

        OrgInvite invite = inviteRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Convite inválido"));

        if (!invite.isActive()) {
            throw new RuntimeException("Convite expirado/revogado/usado");
        }

        User user = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        membershipRepository
                .findByOrganization_IdAndUser_Id(invite.getOrganization().getId(), actorId)
                .ifPresent(m -> { throw new RuntimeException("Você já é membro dessa org"); });

        Membership membership = Membership.builder()
                .organization(invite.getOrganization())
                .user(user)
                .role(invite.getRole())
                .build();

        membershipRepository.save(membership);

        invite.setAcceptedAt(Instant.now());
        inviteRepository.save(invite);

        return membership;
    }

    public String generateToken() {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}