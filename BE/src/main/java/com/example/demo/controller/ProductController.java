package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/product")
    public List<Product> getProductsByType(@RequestParam String type) {
        return productRepository.findByType(type);
    }

    @GetMapping("/top")
    public List<Product> getTopProducts() {
        return productRepository.findTop5ByOrderByPurchaseCountDesc();
    }
}