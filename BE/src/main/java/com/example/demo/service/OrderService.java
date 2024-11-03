package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.Coupon;
import com.example.demo.model.Orders;
import com.example.demo.repo.CouponRepository;
import com.example.demo.repo.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CouponRepository couponRepository;
    public int addOrder(Account account, Coupon coupon, Date orderDate, int status, double totalPrice, int totalQuantity) {
        Orders order = new Orders();
        order.setAccount(account);
        order.setCoupon(coupon);
        order.setOrderDate(orderDate);
        order.setStatus(status);
        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);

        orderRepository.save(order);

        if(coupon!=null){
            coupon.setUsageCount(coupon.getUsageCount()+1);
            couponRepository.save(coupon);
        }

        return order.getOrderId();
    }
}
