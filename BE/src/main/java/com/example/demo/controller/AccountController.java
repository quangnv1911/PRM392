package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;

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

    @PostMapping("/updateAccountPayment")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> account) {
        String accountId = account.get("accountId");
        String fullName = account.get("fullname");
        String phone = account.get("phone");
        String address = account.get("address");

        Optional<Account> acc = accountService.updateAccountPayment(accountId, fullName, phone, address);
        if (acc.isPresent()) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

}
