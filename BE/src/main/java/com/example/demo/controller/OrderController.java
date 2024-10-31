// src/main/java/com/example/demo/controller/OrderController.java
package com.example.demo.controller;

import com.example.demo.model.OrderDetail;
import com.example.demo.model.Orders;
import com.example.demo.model.User;
import com.example.demo.repo.OrderDetailRepository;
import com.example.demo.repo.OrdersRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/checkPurchase")
    public ResponseEntity<Boolean> checkPurchase(@RequestParam String username, @RequestParam Integer productId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Long userId = user.get().getId();
            List<Orders> orders = ordersRepository.findByAccountUserId(userId);
            for (Orders order : orders) {
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());
                for (OrderDetail orderDetail : orderDetails) {
                    if (orderDetail.getProduct().getProductId().equals(productId)) {
                        return ResponseEntity.ok(true);
                    }
                }
            }
        }
        return ResponseEntity.ok(false);
    }
}