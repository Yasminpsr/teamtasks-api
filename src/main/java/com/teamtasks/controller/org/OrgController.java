package com.teamtasks.controller.org;

import com.teamtasks.dto.org.*;
import com.teamtasks.service.org.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs")
public class OrgController {

    private final OrganizationService orgService;

    public OrgController(OrganizationService orgService) {
        this.orgService = orgService;
    }

    @PostMapping
    public OrgResponse create(@RequestBody @Valid CreateOrgRequest req, Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        var org = orgService.createOrg(userId, req);
        return new OrgResponse(org.getId(), org.getName());
    }

    @GetMapping
    public List<OrgResponse> myOrgs(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return orgService.myOrgs(userId).stream()
                .map(o -> new OrgResponse(o.getId(), o.getName()))
                .toList();
    }

    @GetMapping("/{orgId}/members")
    public List<MemberResponse> members(@PathVariable UUID orgId, Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        orgService.assertMember(orgId, userId);

        return orgService.orgMembers(orgId).stream()
                .map(m -> new MemberResponse(
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getUser().getEmail(),
                        m.getRole()
                ))
                .toList();
    }
}