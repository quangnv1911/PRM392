package com.example.demo.controller;

import com.example.demo.model.Coupon;
import com.example.demo.repo.CouponRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/coupon")
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return couponRepository.save(coupon);
    }

    // Cập nhật mã giảm giá
    @PutMapping("/coupon/{id}")
    public Coupon updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        return couponRepository.save(coupon);
    }

    // Xóa mã giảm giá
    @DeleteMapping("/coupon/{id}")
    public void deleteCoupon(@PathVariable Integer id) {
        couponRepository.deleteById(Long.valueOf(id));
    }
}
