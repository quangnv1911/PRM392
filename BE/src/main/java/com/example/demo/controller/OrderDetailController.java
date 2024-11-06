package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Coupon;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.OrderDetailRequest;
import com.example.demo.model.Product;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.CouponRepository;
import com.example.demo.repo.OrderDetailRepository;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.service.OrderDetailService;
import com.example.demo.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @PostMapping("/createOrderDetail")
    public ResponseEntity<String> createOrderList(@RequestBody ArrayList<OrderDetailRequest> list) {
        try {
            // Xử lý danh sách orderDetails
            for (OrderDetailRequest orderDetail : list) {
                // Xử lý từng OrderDetailRequest
                System.out.println("Order ID: " + orderDetail.getOrderId());
                System.out.println("Product ID: " + orderDetail.getProductId());
                System.out.println("Unit Price: " + orderDetail.getUnitPrice());
                System.out.println("Quantity: " + orderDetail.getQuantity());
                System.out.println("Size: " + orderDetail.getSize());
                System.out.println("Color: " + orderDetail.getColor());

                orderDetailService.createOrderDetail(orderDetail);
            }
            return ResponseEntity.ok("Đặt hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Đặt hàng không thành công!");
        }
    }

    @GetMapping("/getOrderDetailByOrderId")
    public ResponseEntity<List<OrderDetail>> getOrderDetailByOrderId(@RequestParam Map<String,String> orderId) {
        int orderid=Integer.parseInt(orderId.get("orderId"));
        List<OrderDetail> list = orderDetailRepository.findByOrderOrderId(orderid);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/order-detail/{id}")
    public ResponseEntity<List<OrderDetail>> getOrderDetail(@PathVariable Integer id) {

        List<OrderDetail> list = orderDetailRepository.findByOrderOrderId(id);
        return ResponseEntity.ok(list);
    }
}
