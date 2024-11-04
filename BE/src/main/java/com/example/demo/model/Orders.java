package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false)
    private Date orderDate;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private Integer status;

    @ManyToOne
    @JoinColumn(nullable = false, name = "accountId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;


}
