package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String productName) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/top")
    public List<Product> getTopProducts() {
        return productRepository.findTop5ByOrderByPurchaseCountDesc();
    }
    @GetMapping("/searchProduct")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String search) {
        List<Product> products = productRepository.findBySearch(search);
        return ResponseEntity.ok(products);
    }
}