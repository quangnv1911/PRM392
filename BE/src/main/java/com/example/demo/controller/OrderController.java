package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Coupon;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.Orders;
import com.example.demo.model.User;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.CouponRepository;
import com.example.demo.repo.OrderDetailRepository;
import com.example.demo.repo.OrdersRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.OrderService;

import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private OrderService orderService;
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

    @PostMapping("/createOrder")
    public ResponseEntity<Map<String, Integer>> createOrder(@RequestBody Map<String, String> order) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        Long accountId = Long.parseLong(order.get("accountid"));
        String couponCode = order.get("couponcode");
        Optional<Account> account = accountRepository.findById(accountId);
        Optional<Coupon> coupon = couponRepository.findByCouponCode(couponCode);
        Date orderdate = formatter.parse(order.get("orderdate"));
        int status = Integer.parseInt(order.get("status"));
        double totalprice = Double.parseDouble(order.get("totalprice"));
        int totalquantity = Integer.parseInt(order.get("totalquantity"));

        Coupon coupon1 = null;
        if (coupon.isPresent()) {
            coupon1 = coupon.get();
        }

        int id = orderService.addOrder(account.get(), coupon1, orderdate, status, totalprice, totalquantity);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/getOrderHistoryByAccId")
    public List<Orders> getOrderHistoryByUserId(@RequestParam String username) throws ParseException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Long userId = user.get().getId();
            List<Orders> orders = ordersRepository.findByAccountUserIdOrderByOrderDateDesc(userId);
            return orders;
        }
        return null;
    }

    @PostMapping("/changeOrderStatus")
    public ResponseEntity<Boolean> changeOrderStatus(@RequestBody Map<String, String> input) throws ParseException {
        int orderid = Integer.parseInt(input.get("orderId"));
        int statusid = Integer.parseInt(input.get("statusId"));
        Optional<Orders> orders = ordersRepository.findByOrderId(orderid);
        if (orders.isPresent()) {
            orders.get().setStatus(statusid);
            ordersRepository.save(orders.get());
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/orders/all")
    public ResponseEntity<List<Orders>> getAllOrder() {

        List<Orders> listOrders = ordersRepository.findAll();

        if (listOrders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(listOrders);
    }

    @PutMapping("/orders/update-status/{id}")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Integer id, @RequestBody Integer statusId) {
        Optional<Orders> orders = ordersRepository.findByOrderId(id);
        if (orders.isPresent()) {
            orders.get().setStatus(statusId);
            ordersRepository.save(orders.get());
            return ResponseEntity.ok(orders.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }



}
