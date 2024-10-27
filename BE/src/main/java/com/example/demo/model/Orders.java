package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate orderDate;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Float totalPrice;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;
}
