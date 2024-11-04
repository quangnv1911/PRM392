package com.example.demo.service;

import com.example.demo.model.OrderDetail;
import com.example.demo.model.OrderDetailRequest;
import com.example.demo.model.Orders;
import com.example.demo.model.Product;
import com.example.demo.repo.AccountRepository;
import com.example.demo.repo.CouponRepository;
import com.example.demo.repo.OrderDetailRepository;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    public void createOrderDetail(OrderDetailRequest orderDetail) {
        OrderDetail orderDetail1 =new OrderDetail();
        orderDetail1.setColor(orderDetail.getColor());
        orderDetail1.setQuantity(orderDetail.getQuantity());
        orderDetail1.setSize(orderDetail.getSize());
        orderDetail1.setUnitPrice(orderDetail.getUnitPrice());

        Optional<Product> product=productRepository.findById(orderDetail.getProductId());
        Optional<Orders> order=orderRepository.findById(orderDetail.getOrderId());

        if(product.isPresent()){
            orderDetail1.setProduct(product.get());
        }

        if(product.isPresent()){
            orderDetail1.setOrder(order.get());
            product.get().setPurchaseCount(product.get().getPurchaseCount()+orderDetail.getQuantity());
            product.get().setStockQuantity(product.get().getStockQuantity()-orderDetail.getQuantity());
            productRepository.save(product.get());
        }
        orderDetailRepository.save(orderDetail1);



    }
}
