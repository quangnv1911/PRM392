package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/product")
    public List<Product> getProductsByType(@RequestParam(required = false) String type) {

        if(type == null){
            return productRepository.findAll();
        }
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

    @GetMapping("")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PutMapping("")
    public void updateProductDetail(@RequestParam int productId, @RequestBody Product productUpdate) {

        productRepository.save(productUpdate);
    @GetMapping("/searchProduct")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String search) {
        List<Product> products = productRepository.findBySearch(search);
        return ResponseEntity.ok(products);
    }
}