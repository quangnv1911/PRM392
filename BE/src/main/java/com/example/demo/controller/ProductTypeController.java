package com.example.demo.controller;

import com.example.demo.model.ProductType;
import com.example.demo.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductTypeController {
    @Autowired
    private ProductTypeService productTypeService;

    @GetMapping("/buttons")
    public List<ProductType> getAllProductTypes() {
        return productTypeService.getAllProductTypes();
    }
}