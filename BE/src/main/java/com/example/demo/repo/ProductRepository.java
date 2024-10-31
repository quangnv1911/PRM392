package com.example.demo.repo;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(String type);

    List<Product> findTop5ByOrderByPurchaseCountDesc();
    List<Product> findByProductNameContaining(String productName);

    @Query("SELECT p FROM Product p WHERE " +
            "p.productName LIKE %:search% OR " +
            "p.type LIKE %:search% OR " +
            "p.category.categoryName LIKE %:search%")
    List<Product> findBySearch(String search);
}