package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "coupon_Type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer discountType;//1-số, 2- phần trăm

    @Column(nullable = false, length = 250)
    private String description;//mô tả kiểu coupon đó, phần trăm, số
}
