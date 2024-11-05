
package com.example.demo.repo;

import com.example.demo.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByAccountUserId(Long userId);
    List<Orders> findByAccountUserIdOrderByOrderDateDesc(Long userId);
    List<Orders> findByAccount_AccountId(Long accountId);
    Optional<Orders> findByOrderId(Integer orderId);
}

