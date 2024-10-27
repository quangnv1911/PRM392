package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.repo.ProductDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductDetailController {
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @GetMapping("/product-detail")
    public List<ProductDetail> getProductsByType(@RequestParam int productId) {
        return productDetailRepository.findByProductId(productId);
    }
}
