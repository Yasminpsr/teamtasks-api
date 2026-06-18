package com.teamtasks.controller.users;

import com.teamtasks.domain.user.User;
import com.teamtasks.dto.user.MeResponse;
import com.teamtasks.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import com.teamtasks.domain.org.Membership;
import com.teamtasks.repository.MembershipRepository;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public UserController(
            UserRepository userRepository,
            MembershipRepository membershipRepository
    ) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @GetMapping("/me")
    public MeResponse me(org.springframework.security.core.Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Membership membership = membershipRepository.findAllByUser_Id(user.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Membership não encontrada"));

        String role = membership.getRole().name();

        return new MeResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                role
        );
    }
}