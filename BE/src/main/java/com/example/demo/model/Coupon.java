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

    // One-to-one với CouponType
    @OneToOne
    @JoinColumn(name = "discountId", nullable = false)
    private CouponType couponType;
}
