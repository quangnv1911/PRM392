package com.example.demo.repo;

import com.example.demo.model.Account;
import com.example.demo.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByAccount_AccountId(Long accountId);
}
