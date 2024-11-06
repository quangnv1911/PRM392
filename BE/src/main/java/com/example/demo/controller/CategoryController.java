package com.example.demo.controller;

import com.example.demo.model.Categorie;
import com.example.demo.model.Coupon;
import com.example.demo.repo.CategoriesRepository;
import com.example.demo.repo.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoriesRepository categoriesRepository;

    @GetMapping("")
    public ResponseEntity<List<Categorie>> getAllCategory() {
        var listCategory = categoriesRepository.findAll();
        if (listCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(listCategory);
    }
}
