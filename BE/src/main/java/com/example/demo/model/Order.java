package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Order")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;
}
