package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_detail")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String imageProduct;

}
