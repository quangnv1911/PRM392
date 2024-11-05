package com.example.pmg302_project.DTOs;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class CouponDTO implements Serializable {
    private Integer id;
    private String couponCode;//mã của coupon
    private Integer discountValue;//value của coupon
    private Integer minOrderValue;//giá tiền nhỏ nhất được áp coupon
    private Integer maxOrderValue;//giá tiền lớn nhất được áp coupon
    private LocalDate startDate;//ngày bắt đầu sử dụng dc coupon
    private LocalDate endDate;//ngày hết hạn
    private Integer usageLimit;//số lượng limit của coupon
    private Integer usageCount;//số lượng người đã dùng
    private Boolean isActive;//có kích hoạt không
    private Integer createdBy;//tạo bởi
    private Integer couponType;//0-phần trăm, 1-sô

    public CouponDTO(){

    }

    public CouponDTO(Integer id, String couponCode, Integer discountValue, Integer minOrderValue, Integer maxOrderValue, LocalDate startDate, LocalDate endDate, Integer usageLimit, Integer usageCount, Boolean isActive, Integer couponType) {
        this.id = id;
        this.couponCode = couponCode;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.maxOrderValue = maxOrderValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
        this.usageCount = usageCount;
        this.isActive = isActive;
        this.couponType = couponType;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(Integer minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public Integer getMaxOrderValue() {
        return maxOrderValue;
    }

    public void setMaxOrderValue(Integer maxOrderValue) {
        this.maxOrderValue = maxOrderValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }
}

