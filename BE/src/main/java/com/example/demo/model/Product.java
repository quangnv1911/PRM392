package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Categorie category;

    private String imageLink;
    private String type;
    private Integer purchaseCount;
    private double rate;

    private Integer createdBy;
}
