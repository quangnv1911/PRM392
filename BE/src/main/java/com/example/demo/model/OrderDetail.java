package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_detail") // Ánh xạ bảng 'orderdetail'
@Data  // Bao gồm @Getter, @Setter, @EqualsAndHashCode, @ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động tăng ID
    @Column( nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer unitPrice;

    // Many-to-one với Order, Product, và Coupon

    @ManyToOne
    @JoinColumn(nullable = false)
    private Orders order;  // Mối quan hệ với bảng Order

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;  // Mối quan hệ với bảng Product

    @ManyToOne
    @JoinColumn(nullable = false)
    private Coupon coupon;  // Mối quan hệ với bảng Coupon
}
