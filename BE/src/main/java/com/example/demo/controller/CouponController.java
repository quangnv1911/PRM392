package com.example.demo.controller;

import com.example.demo.Dtos.Request.CouponCreateDTO;
import com.example.demo.model.Coupon;
import com.example.demo.repo.CouponRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
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

    @PostMapping("/coupons")
    public Coupon createCoupon(@RequestBody CouponCreateDTO couponDTO) {
        Coupon newCoupon = new Coupon();

        // Map each field from DTO to entity
        newCoupon.setCouponCode(couponDTO.getCouponCode());
        newCoupon.setDiscountValue(couponDTO.getDiscountValue());
        newCoupon.setMinOrderValue(couponDTO.getMinOrderValue());
        newCoupon.setMaxOrderValue(couponDTO.getMaxOrderValue());

        // Convert Date to LocalDate for startDate and endDate
        if (couponDTO.getStartDate() != null) {
            newCoupon.setStartDate(couponDTO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if (couponDTO.getEndDate() != null) {
            newCoupon.setEndDate(couponDTO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        newCoupon.setUsageLimit(couponDTO.getUsageLimit());
        newCoupon.setUsageCount(couponDTO.getUsageCount());
        newCoupon.setIsActive(true);
        newCoupon.setCreatedBy(0);
        newCoupon.setCouponType(couponDTO.getCouponType());

        // Save the new Coupon entity
        return couponRepository.save(newCoupon);
    }

    // Cập nhật mã giảm giá
    @PutMapping("/coupons/{id}")
    public Coupon updateCoupon(@PathVariable Integer id, @RequestBody CouponCreateDTO couponDTO) {

        // Find the existing coupon entity by id
        Optional<Coupon> existingCouponOpt = couponRepository.findById(Long.valueOf(id));
        if (existingCouponOpt.isPresent()) {
            Coupon existingCoupon = existingCouponOpt.get();

            // Map each field from DTO to entity
            existingCoupon.setCouponCode(couponDTO.getCouponCode());
            existingCoupon.setDiscountValue(couponDTO.getDiscountValue());
            existingCoupon.setMinOrderValue(couponDTO.getMinOrderValue());
            existingCoupon.setMaxOrderValue(couponDTO.getMaxOrderValue());

            // Convert Date to LocalDate for startDate and endDate
            if (couponDTO.getStartDate() != null) {
                existingCoupon.setStartDate(couponDTO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if (couponDTO.getEndDate() != null) {
                existingCoupon.setEndDate(couponDTO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            existingCoupon.setUsageLimit(couponDTO.getUsageLimit());
            existingCoupon.setUsageCount(couponDTO.getUsageCount());
            existingCoupon.setIsActive(couponDTO.getIsActive());
            existingCoupon.setCreatedBy(couponDTO.getCreatedBy());
            existingCoupon.setCouponType(couponDTO.getCouponType());

            return couponRepository.save(existingCoupon);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon with id " + id + " not found.");
        }
    }


    // Xóa mã giảm giá
    @DeleteMapping("/coupons/{id}")
    public void deleteCoupon(@PathVariable Integer id) {
        couponRepository.deleteById(Long.valueOf(id));
    }
}
