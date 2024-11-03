package com.example.demo.controller;

import com.example.demo.model.Coupon;
import com.example.demo.repo.CouponRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CouponController {
    @Autowired
    private CouponRepository couponRepository;

    @GetMapping("/coupondetail")
    public Optional<Coupon> getCouponByCouponCode(@RequestParam String couponcode) {
        return couponRepository.findByCouponCode(couponcode);
    }

    @GetMapping("/coupons")
    public List<Coupon> getCouponList() {
        return couponRepository.findAll();
    }
}
