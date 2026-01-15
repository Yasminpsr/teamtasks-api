package com.teamtasks.controller.users;

import com.teamtasks.domain.user.User;
import com.teamtasks.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public User me(org.springframework.security.core.Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}