package com.example.demo.controller;

import com.example.demo.Dtos.Response.UserResponseDTO;
import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.jwt.JwtUtil;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String loginMethod = credentials.get("method"); // "google" or "email"
        String fullName = credentials.get("fullName");

        Optional<User> user = userService.login(username, password, loginMethod, fullName);
        if (user.isPresent()) {
            String token = jwtUtil.generateToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);
            Account account = accountRepository.findByUserId(user.get().getId());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("username", user.get().getUsername());
            response.put("role", account.getRole().getRoleName());
            response.put("fullName", user.get().getFullName());
            response.put("userId", user.get().getId().toString());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(null);

        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> userDetails) {
        String username = userDetails.get("username");
        String fullName = userDetails.get("fullName");

        Optional<User> user = userService.register(username, fullName);
        if (user.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("username", user.get().getUsername());
            response.put("fullName", user.get().getFullName());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> userDetails) {
        String email = userDetails.get("email");

        boolean result = userService.resetPassword(email);
        if (result) {
            return ResponseEntity.ok("Check your email to reset password");
        } else {
            return ResponseEntity.status(404).body("Failed to reset password");
        }
    }

    @GetMapping("users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        var users = accountRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(user -> new UserResponseDTO(
                        user.getUser().getId(),
                        user.getUser().getUsername(),
                        user.getUser().getFullName(),
                        user.getImage() // Giả sử User có trường image
                ))
                .collect(Collectors.toList());

        if (!userResponseDTOs.isEmpty()) {
            return ResponseEntity.ok(userResponseDTOs);
        } else {
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
    }
}
