package com.teamtasks.controller.org;

import com.teamtasks.domain.org.OrgRole;
import com.teamtasks.dto.org.*;
import com.teamtasks.repository.OrgInviteRepository;
import com.teamtasks.service.org.InviteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs")
public class InviteController {

    private final InviteService inviteService;
    private final OrgInviteRepository inviteRepository;

    public InviteController(InviteService inviteService, OrgInviteRepository inviteRepository) {
        this.inviteService = inviteService;
        this.inviteRepository = inviteRepository;
    }

    @PostMapping("/{orgId}/invites")
    public InviteCreatedResponse create(
            @PathVariable UUID orgId,
            @RequestBody @Valid CreateInviteRequest req,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        var created = inviteService.createInvite(orgId, actorId, req);

        String token = created.rawToken();
        var invite = created.invite();

        String link = "http://localhost:3000/invites/accept?token=" + token; // front depois

        return new InviteCreatedResponse(
                invite.getId(),
                invite.getEmail(),
                invite.getRole(),
                invite.getExpiresAt(),
                token,
                link
        );
    }

    @GetMapping("/{orgId}/invites")
    public List<InviteResponse> list(@PathVariable UUID orgId, Authentication auth) {
        // Por simplicidade: qualquer membro pode listar
        return inviteRepository.findAllByOrganization_Id(orgId).stream()
                .map(i -> new InviteResponse(
                        i.getId(),
                        i.getEmail(),
                        i.getRole(),
                        i.getExpiresAt(),
                        i.getAcceptedAt(),
                        i.getRevokedAt()
                )).toList();
    }

    @PatchMapping("/{orgId}/invites/{inviteId}/role")
    public void updateRole(
            @PathVariable UUID orgId,
            @PathVariable UUID inviteId,
            @RequestParam OrgRole role,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        inviteService.updateInviteRole(orgId, inviteId, actorId, role);
    }

    @DeleteMapping("/{orgId}/invites/{inviteId}")
    public void revoke(
            @PathVariable UUID orgId,
            @PathVariable UUID inviteId,
            Authentication auth
    ) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        inviteService.revokeInvite(orgId, inviteId, actorId);
    }

    @PostMapping("/invites/accept")
    public void accept(@RequestBody @Valid AcceptInviteRequest req, Authentication auth) {
        UUID actorId = UUID.fromString((String) auth.getPrincipal());
        inviteService.acceptInvite(actorId, req.token());
    }
}