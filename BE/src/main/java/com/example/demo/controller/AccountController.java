package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/account")
    public Account getAccountByUserName(@RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);

        // Check if user is present
        if (user.isPresent()) {
            Account account = accountRepository.findByUserId(user.get().getId());

            // Check if account is found
            if (account != null) {
                return account;
            }
        }
        return null;
    }

}
