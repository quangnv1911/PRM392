package com.example.demo.repo;

import com.example.demo.model.Coupon;
import com.example.demo.model.ProductDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Coupon findByCouponCode(String couponcode);
}
