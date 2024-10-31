package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.repo.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

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

    public Account updateAccountDetails(Account account, String fullName, String phone, String address) {
        account.setFullname(fullName);
        account.setPhone(phone);
        account.setAddress(address);
        return accountRepository.save(account);
    }
}
