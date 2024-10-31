package com.example.demo.repo;

import com.example.demo.model.Categorie;
import com.example.demo.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository  extends JpaRepository<Categorie, Long> {
    Categorie findByCategoryId(Integer categoryId);
}
