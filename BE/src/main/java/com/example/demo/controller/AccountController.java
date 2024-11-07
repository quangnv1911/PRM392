package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/account/by-userid/{userId}")
    public ResponseEntity<Account> getAccountByUserId(@PathVariable Long userId) {
        Account account = accountRepository.findByUserId(userId);

        // Check if account is found
        if (account != null) {
            return ResponseEntity.ok(account); // Return the found account
        }
        return ResponseEntity.notFound().build(); // Return 404 if account not found
    }

    @GetMapping("/id/{username}")
    public ResponseEntity<Integer> getAccountId(@PathVariable String username) {
        Integer accountId = accountService.getAccountIdByUsername(username);
        if (accountId != null) {
            return ResponseEntity.ok(accountId);
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }
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

    @PostMapping("/account/update-profile")
    public ResponseEntity<Map<String, String>> updateAccount(@RequestBody Map<String, String> accountDetails) {
        String username = accountDetails.get("username");
        String fullName = accountDetails.get("fullname");
        String phone = accountDetails.get("phone");
        String address = accountDetails.get("address");

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Long userId = user.get().getId();
            User existingUser = user.get();
            existingUser.setFullName(fullName);
            userRepository.save(existingUser);

            Optional<Account> account = Optional.ofNullable(accountRepository.findByUserId(userId));
            if (account.isPresent()) {
                Account updatedAccount = accountService.updateAccountDetails(account.get(), fullName, phone, address);
                if (updatedAccount != null) {
                    return ResponseEntity.ok(null);
                }
            }
        }
        return ResponseEntity.status(404).body(null);
    }

}
