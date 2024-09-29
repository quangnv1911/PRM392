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
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String imageLink;

    private String type;

    private int purchaseCount;

    private double rate;
}
