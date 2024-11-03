package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupon")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( nullable = false)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String couponCode;//mã của coupon

    @Column(nullable = false)
    private Integer discountValue;//value của coupon

    @Column(nullable = false)
    private Integer minOrderValue;//giá tiền nhỏ nhất được áp coupon

    @Column(nullable = false)
    private Integer maxOrderValue;//giá tiền lớn nhất được áp coupon

    @Column( nullable = false)
    private LocalDate startDate;//ngày bắt đầu sử dụng dc coupon

    @Column( nullable = false)
    private LocalDate endDate;//ngày hết hạn

    @Column(nullable = false)
    private Integer usageLimit;//số lượng limit của coupon

    @Column(nullable = false)
    private Integer usageCount;//số lượng người đã dùng

    @Column( nullable = false)
    private Boolean isActive;//có kích hoạt không

    @Column(nullable = false)
    private Integer createdBy;//tạo bởi

    @Column(nullable = false)
    private Integer couponType;//0-phần trăm, 1-sô

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
