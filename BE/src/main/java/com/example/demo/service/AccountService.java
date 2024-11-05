package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repo.AccountRepository;

import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    public Optional<Account> updateAccountPayment(String accountId, String fullName, String phone, String address) {

        Optional<Account> acc = accountRepository.findById(Long.parseLong(accountId));
        if (acc.isPresent()) {
            Account existingAcc = acc.get();
            existingAcc.setFullname(fullName);
            existingAcc.setPhone(phone);
            existingAcc.setAddress(address);
            accountRepository.save(existingAcc);
            return Optional.of(existingAcc);
        }
        return Optional.empty();

    }

    public Integer getAccountIdByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            Account account = accountRepository.findByUserId(user.getId());
            if (account != null) {
                return account.getAccountId();
            }
        }
        return null; // or throw an exception if user/account not found


    public Account updateAccountDetails(Account account, String fullName, String phone, String address) {
        account.setFullname(fullName);
        account.setPhone(phone);
        account.setAddress(address);
        return accountRepository.save(account);
    }
}
