package com.example.demo.repo;

import com.example.demo.model.ProductDetail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    List<ProductDetail> findByProductId(Long prodcutId);
    void deleteProductDetailByProductId(Long productId);
    boolean existsByProductId(Long productId);
}
