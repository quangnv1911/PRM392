package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Orders;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    // 1. Get all customers
    @GetMapping
    public List<Account> getAllCustomers() {
        return accountRepository.findAll();
    }

    // 2. Get a customer by ID, including purchase history
    @GetMapping("/{id}")
    public Account getCustomerById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.orElse(null); // Handle null if customer is not found
    }

    // 3. Get customer purchase history
// In your controller
    @GetMapping("/{id}/orders")
    public ResponseEntity<Map<String, Object>> getCustomerOrdersWithTotal(@PathVariable Long id) {
        List<Orders> orders = ordersRepository.findByAccount_AccountId(id);
        double totalPurchaseAmount = orders.stream()
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("totalPurchaseAmount", totalPurchaseAmount);

        return ResponseEntity.ok(response);
    }

}
